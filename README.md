Stripe Spring Boot Demo
- 商品頁 → 選擇商品 → 付款頁 → 訂單頁
- Docker / 傳統啟動皆可
- API Key 可用環境變數管理
- 商品與價格查詢加入 Redis 快取，避免每次查詢都回源資料庫

## 開發環境沒有 Redis 的替代方案
- 預設使用 Redis 快取（`spring.cache.type=redis`）
- 若開發機沒有 Redis，可用以下方式：
  - 啟用 dev profile（改用 in-memory cache）
    - `SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run`
    - 或 `SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run`
  - 直接覆寫快取類型
    - `SPRING_CACHE_TYPE=simple`：使用 Spring 內建 ConcurrentMap 快取
    - `SPRING_CACHE_TYPE=none`：關閉快取，直接讀資料來源
