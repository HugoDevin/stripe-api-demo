# Enterprise E-Commerce Backend (Java 21 + Spring Boot 3)

Modular Monolith + Event-Driven + Outbox + RabbitMQ + Stripe PaymentIntent.

## Modules
- `catalog`, `inventory`, `order`, `payment`, `invoicing`, `fulfillment`, `notification`, `accounting`, `reporting`, `messaging`, `common`

## Quick Start (Docker Compose)
1. Copy env:
   ```bash
   cp .env.example .env
   ```
2. Start:
   ```bash
   docker compose up --build
   ```
3. Services:
   - App: `http://localhost:8080`
   - RabbitMQ UI: `http://localhost:15672` (guest/guest)
   - PostgreSQL: `localhost:5432`
   - Redis: `localhost:6379`

## Profiles
- `prod`: real Stripe gateway + webhook endpoint
- `staging`: real Stripe gateway + webhook endpoint
- `dev-offline`: fake payment gateway + internal simulate endpoints (no external Stripe dependency)

All timestamps are UTC (`OffsetDateTime.now()`).

## Core APIs
### Catalog / Inventory
- `POST /api/products`
- `GET /api/products`
- `GET /api/products/{sku}`
- `GET /api/inventory/{sku}`
- `POST /api/inventory/{sku}/adjust`

### Orders
- `POST /api/orders`
- `GET /api/orders/{id}`

### Payments
- `POST /api/payments/create?orderId=...`
- `GET /api/payments/{id}`

### Stripe webhook (staging/prod)
- `POST /api/stripe/webhook`

### Reporting
- `GET /api/reporting/sales-daily?from=YYYY-MM-DD&to=YYYY-MM-DD`

## dev-offline simulate payment
Only available in `dev-offline` profile.

Headers:
- `X-DEV-TOKEN: <DEV_INTERNAL_TOKEN>`

Endpoints:
- `POST /internal/payments/{orderId}/simulate-success`
- `POST /internal/payments/{orderId}/simulate-fail`

## Example Flow (dev-offline)
1. Create product:
   ```bash
   curl -X POST localhost:8080/api/products -H 'Content-Type: application/json' -d '{"sku":"BOOK-001","name":"Book","price":75000,"currency":"TWD"}'
   ```
2. Add inventory:
   ```bash
   curl -X POST localhost:8080/api/inventory/BOOK-001/adjust -H 'Content-Type: application/json' -d '{"delta":10}'
   ```
3. Create order:
   ```bash
   curl -X POST localhost:8080/api/orders -H 'Content-Type: application/json' -d '{"customerEmail":"user@example.com","items":[{"sku":"BOOK-001","qty":1}]}'
   ```
4. Create payment:
   ```bash
   curl -X POST 'localhost:8080/api/payments/create?orderId=<ORDER_ID>'
   ```
5. Simulate success:
   ```bash
   curl -X POST localhost:8080/internal/payments/<ORDER_ID>/simulate-success -H 'X-DEV-TOKEN: dev-token'
   ```
6. Verify projections:
   - `receipts`
   - `fulfillments`
   - `notifications`
   - `accounting_entries`
   - `sales_daily_fact`

## Stripe test mode (staging/prod)
1. Backend calls `POST /api/payments/create?orderId=...` to get `clientSecret`.
2. Frontend uses Stripe.js `confirmCardPayment(clientSecret, ...)`.
3. Stripe sends webhook to `/api/stripe/webhook`.
4. Backend verifies signature + idempotency and writes outbox event.

## Architecture Notes
- Amount is always server-side calculated from product price.
- Inventory reserve is atomic (`available_qty >= qty`) so no negative stock.
- Payment succeeded/failed side effects are event-driven through RabbitMQ consumers.
- Outbox guarantees DB state + event publish consistency.
- Consumers are idempotent via `processed_events(event_id, consumer_name)`.

## Common Troubleshooting
- **Webhook signature failed**: check `STRIPE_WEBHOOK_SECRET` and raw request body passthrough.
- **Stock 409**: expected when reserve fails due insufficient stock.
- **Duplicate event**: verify `processed_events` and `webhook_events.provider_event_id` uniqueness.
- **No message consumed**: verify RabbitMQ exchange `domain.events` and queue bindings.
