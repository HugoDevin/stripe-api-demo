# Stripe Event-Driven Ecommerce Backend (Java 21 / Spring Boot 3)

## Run with Docker Compose
```bash
cp .env.example .env
./scripts/docker-engine-up.sh
```
Services:
- App: http://localhost:8080
- RabbitMQ UI: http://localhost:15672 (guest/guest)
- PostgreSQL: localhost:5432

All timestamps are UTC (`hibernate.jdbc.time_zone=UTC`).

## dev-offline profile
- `SPRING_PROFILES_ACTIVE=dev-offline`
- payment gateway = `FakePaymentGateway`
- use internal simulate endpoints with `X-DEV-TOKEN: $DEV_INTERNAL_TOKEN`

### Demo flow (offline)
```bash
curl -X POST localhost:8080/api/products -H 'Content-Type: application/json' -d '{"sku":"SKU-1","name":"Book","price":100,"currency":"USD","active":true}'
curl -X POST 'localhost:8080/api/inventory/SKU-1/adjust?qty=10'
curl -X POST localhost:8080/api/orders -H 'Content-Type: application/json' -d '{"customerEmail":"a@demo.com","items":[{"sku":"SKU-1","qty":1}]}'
curl -X POST 'localhost:8080/api/payments/create?orderId=<ORDER_ID>'
curl -X POST localhost:8080/internal/payments/<ORDER_ID>/simulate-success -H 'X-DEV-TOKEN: dev-token'
```
Check generated tables: `receipts`, `fulfillments`, `notifications`, `accounting_entries`, `sales_daily_fact`.

For failed flow:
```bash
curl -X POST localhost:8080/internal/payments/<ORDER_ID>/simulate-fail -H 'X-DEV-TOKEN: dev-token'
```
Reservation will be released and order canceled.

## staging/prod Stripe flow
1. `POST /api/orders` create order.
2. `POST /api/payments/create?orderId=...` get `clientSecret`.
3. Frontend confirms PaymentIntent with Stripe.js.
4. Stripe sends webhook to `POST /api/stripe/webhook`.
5. Server verifies signature + idempotency (`webhook_events.provider_event_id` unique), then writes outbox event.

## API list
- `POST /api/products`, `GET /api/products`, `GET /api/products/{sku}`
- `GET /api/inventory/{sku}`, `POST /api/inventory/{sku}/adjust?qty=`
- `POST /api/orders`, `GET /api/orders/{id}`
- `POST /api/payments/create?orderId=`, `GET /api/payments/{id}`
- `POST /api/stripe/webhook` (staging/prod only)
- `GET /api/reporting/sales-daily?from=YYYY-MM-DD&to=YYYY-MM-DD`
- `POST /internal/payments/{orderId}/simulate-success|simulate-fail` (dev-offline only)

## Troubleshooting
- Webhook signature failure: verify `STRIPE_WEBHOOK_SECRET` and raw payload forwarding.
- 409 insufficient stock: reservation atomic update failed.
- Idempotent redelivery: see `processed_events` table; duplicate events are ignored per consumer.
- If Docker daemon is unreachable, verify Docker Engine is running and socket exists: `ls -l /var/run/docker.sock`.
- Use non-Desktop startup command: `./scripts/docker-engine-up.sh` (forces `DOCKER_HOST=unix:///var/run/docker.sock`, auto uses `docker compose` or fallback `docker-compose`).
- If you see `unknown flag: --build` after running script, your Docker Compose plugin is missing. Install `docker-compose-plugin` or `docker-compose` binary.
- `/usr/bin/env: ‘bash\r’: No such file or directory`: run `sed -i "s/\r$//" scripts/*.sh` once; `.gitattributes` already enforces LF for `.sh`.
