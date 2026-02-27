# Enterprise E-Commerce Backend (Java 21 + Spring Boot 3, Thymeleaf-first)

Modular Monolith + DDD boundaries + Event-Driven (Outbox + RabbitMQ) + Stripe PaymentIntent.

## Stack
- Java 21, Spring Boot 3.3.x
- Spring Web + Validation + Thymeleaf
- Spring Data JPA + Flyway
- PostgreSQL, Redis
- RabbitMQ (domain events)
- Stripe Java SDK (prod/staging)

## Runtime Profiles
- `prod`: StripePaymentGateway + webhook
- `staging`: StripePaymentGateway + webhook
- `dev-offline`: FakePaymentGateway + simulate API（不連外）

## Quick Start
```bash
cp .env.example .env
docker compose up --build
```

Services:
- App: `http://localhost:8080/web`
- RabbitMQ Management: `http://localhost:15672` (guest/guest)
- Postgres: `localhost:5432`
- Redis: `localhost:6379`

## Thymeleaf UI (Primary)
- `GET /web` 商品頁
- `POST /web/orders` 建立訂單（後端計價 + reserve 庫存）
- `GET /web/orders` 訂單列表
- `GET /web/orders/{orderId}` 訂單詳情
- `POST /web/payments/create` 建立 PaymentIntent
- `GET /web/payments/{paymentId}` 付款頁（顯示 client secret / dev 提示）
- `dev-offline` 額外提供 Web simulate 按鈕（成功/失敗）

## API Endpoints
- Products: `POST /api/products`, `GET /api/products`, `GET /api/products/{sku}`
- Inventory: `GET /api/inventory/{sku}`, `POST /api/inventory/{sku}/adjust`
- Orders: `POST /api/orders`, `GET /api/orders/{id}`
- Payments: `POST /api/payments/create?orderId=...`, `GET /api/payments/{id}`
- Stripe webhook: `POST /api/stripe/webhook` (staging/prod)
- Reporting: `GET /api/reporting/sales-daily?from=YYYY-MM-DD&to=YYYY-MM-DD`

## dev-offline simulate API
Only in `dev-offline` profile, guarded by header token.
- `POST /internal/payments/{orderId}/simulate-success`
- `POST /internal/payments/{orderId}/simulate-fail`
- Header: `X-DEV-TOKEN: <DEV_INTERNAL_TOKEN>`

## Key Guarantees
- 金額由後端計算（不信任前端）
- 庫存保留使用原子更新，避免負庫存
- webhook 驗簽 + provider event idempotency
- DB 更新 + outbox 寫入同交易
- consumer 透過 `processed_events(event_id, consumer_name)` 冪等
- 時間一律 UTC (`OffsetDateTime`)

## Troubleshooting
- 409 `insufficient stock`: reserve 原子更新失敗，屬預期保護
- Stripe webhook signature fail: 檢查 `STRIPE_WEBHOOK_SECRET`
- 重複事件: 檢查 `webhook_events.provider_event_id`、`processed_events` unique key
- 事件未消費: 檢查 RabbitMQ exchange `domain.events` 與 queue binding
