Stripe Spring Boot Demo
- 商品頁 → 選擇商品 → 付款頁 → 訂單頁
- Docker / 傳統啟動皆可
- API Key 可用環境變數管理
- 商品與價格查詢加入 Redis 快取，避免每次查詢都回源資料庫

## REST API（RFC 導向）
- `POST /api/orders`：建立訂單，回傳 `201 Created` 與 `Location` header。
- `GET /api/orders`：取得訂單清單，回傳 `200 OK`。
- `GET /api/orders/{orderId}`：取得單筆訂單，回傳 `200 OK`。
- `PATCH /api/orders/{orderId}`：更新訂單狀態，成功回傳 `204 No Content`。
- `POST /api/webhooks/stripe`：接收 webhook，回傳 `202 Accepted`。
- 錯誤回應使用 `application/problem+json`（RFC 7807）。
