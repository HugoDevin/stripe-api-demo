# Stripe Event-Driven Ecommerce Backend (Java 21 / Spring Boot 3)

## Run with Docker Compose (Docker Engine)
```bash
cp .env.example .env
./scripts/docker-engine-up.sh
```
Services:
- App/API: http://localhost:8080
- Admin Console: http://localhost:8080/admin
- RabbitMQ UI: http://localhost:15672 (guest/guest)

## Admin Console (Thymeleaf + Spring Security)
- Login: `/admin/login`
- Register: `/admin/register`
- Verify email: `/admin/verify-email?token=...`
- Dashboard: `/admin/dashboard`
- Products CRUD: `/admin/products`
- Inventory adjust/query: `/admin/inventory`
- User management (SUPER only): `/admin/users`
- Dev email outbox (dev-offline + SUPER only): `/admin/dev/emails`

### Default admin accounts (dev-offline, idempotent initializer)
- Super admin: `admin@example.com` / `Admin123!`
- Staff admin: `staff@example.com` / `Staff123!`
- Passwords are stored as **bcrypt hash** in DB.

### Admin activation flow
1. Register from `/admin/register` (email/password/name)
2. System issues one-time token (24h), sends verify URL
3. Open verify URL => `email_verified=true`
4. SUPER ADMIN enables account at `/admin/users/{id}`
5. Only `enabled=true && email_verified=true && !locked` can login

## Security Notes
- `/admin/**` uses form-login + session
- Roles:
  - `ROLE_ADMIN_SUPER`: user management + products/inventory + dev email page
  - `ROLE_ADMIN`: products/inventory only
- Admin IP allowlist (app-layer) via `admin.allowed-cidrs`
  - default: `127.0.0.1/32,::1/128,10.0.0.0/8,192.168.0.0/16,172.16.0.0/12`
  - configurable by env: `ADMIN_ALLOWED_CIDRS`
- In reverse proxy setups, app checks `X-Forwarded-For` first.

## API demo flow (dev-offline)
```bash
curl -X POST localhost:8080/api/products -H 'Content-Type: application/json' -d '{"sku":"SKU-1","name":"Book","price":100,"currency":"USD","active":true}'
curl -X POST 'localhost:8080/api/inventory/SKU-1/adjust?qty=10'
curl -X POST localhost:8080/api/orders -H 'Content-Type: application/json' -d '{"customerEmail":"a@demo.com","items":[{"sku":"SKU-1","qty":1}]}'
curl -X POST 'localhost:8080/api/payments/create?orderId=<ORDER_ID>'
curl -X POST localhost:8080/internal/payments/<ORDER_ID>/simulate-success -H 'X-DEV-TOKEN: dev-token'
```

## Troubleshooting
- `dev-offline` 下預設不啟動 RabbitMQ listeners（避免本機未啟 RabbitMQ 時狂刷連線錯誤）；改由本機 outbox dispatcher 直接處理事件，流程仍會跑完。
- IntelliJ local run **不要**同時啟用 `test` profile（除非你就是要跑測試資料源）；一般啟動請用 `dev-offline`。若你仍要 `test` profile，專案已提供 H2 runtime 以避免 `org.h2.Driver` 缺失。
- Active profile format must be a comma-separated profile list only (e.g. `SPRING_PROFILES_ACTIVE=dev-offline`). Do **not** append other env vars into this same value.
- If IDE logs show profiles like `"SPRING_PROFILES_ACTIVE=dev-offline"` or `"STRIPE_SECRET_KEY=..."` as active profiles, your Run Configuration is misconfigured: put these in **Environment Variables**, not in **Active profiles** field.
- `unknown flag: --build`: install `docker-compose-plugin` or `docker-compose` binary.
- `/usr/bin/env: ‘bash\r’: No such file or directory`: run `sed -i "s/\r$//" scripts/*.sh` once.
- Admin blocked with 403: check `ADMIN_ALLOWED_CIDRS` and source IP / `X-Forwarded-For`.
