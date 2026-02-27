# Stripe Spring Boot + Vue3 Demo

此專案支援 **Vue3 與 Thymeleaf 共存**，並已加上模組化分層，避免 API Controller 與頁面 Controller 混在一起。

## 模組化分層
- `controller.api`：對外 REST API（`/api/**`）
- `controller.web`：Thymeleaf 頁面路由（`/web/**`）
- `application`：用例流程協調（`CheckoutApplicationService`）
- `domain.order`：訂單領域邏輯（`OrderService`）
- `service` / `repository`：外部整合與資料來源（Stripe、商品目錄等）

## 資料庫與快取
- 正式環境預設使用 **PostgreSQL**（`spring.datasource.*`）。
- `dev` profile 使用 **H2（PostgreSQL 模式）** 方便本機開發。
- `src/main/resources/data-dev.sql` 已提供 H2 測試資料（商品 + 使用者訂購紀錄）。
- 商品購物清單 API 透過 Spring Cache 快取，避免每次都重新查詢 DB。

## 路由規劃
- `/`：導向 `/web`
- `/web/**`：Thymeleaf 頁面
- `/api/**`：JSON API（供 Vue3 使用）
- `/admin`：商品管理後台（需登入）

## Backend 啟動
```bash
mvn spring-boot:run
```

必要環境變數：
- `STRIPE_SECRET_KEY`
- `STRIPE_PUBLISHABLE_KEY`

可選環境變數：
- `SPRING_PROFILES_ACTIVE`（設為 `dev` 時改用 H2）
- `FRONTEND_ORIGIN`（預設 `http://localhost:5173`）
- `SPRING_CACHE_TYPE`（`redis` / `simple` / `none`）
- `APP_ADMIN_USERNAME`（後台帳號，預設 `admin`）
- `APP_ADMIN_PASSWORD`（後台密碼，預設 `admin123`）

## 後台功能
- 支援商品價格修改
- 支援商品上架 / 下架
- 下架商品不會出現在前台購買清單，也不能被建立結帳

## Frontend 技術堆疊結構
- `src/api/`：Axios 請求封裝
- `src/types/`：TypeScript 介面/型別定義
- `src/store/`：Pinia（State / Actions）
- `src/views/`：SPA 各個頁面
- `src/components/`：Element Plus 二次封裝組件
- `src/router/`：前端路由設定

## Frontend（Vue3）啟動
```bash
cd frontend
npm install
npm run dev
```

若後端非預設位置，可在前端使用：
```bash
VITE_API_BASE_URL=http://localhost:8080/api npm run dev
```

## API 清單
- `GET /api/config`
- `GET /api/products`
- `POST /api/checkout`（可帶 `customerId` 紀錄使用者）
- `POST /api/orders/{orderId}/complete`
- `GET /api/orders`
