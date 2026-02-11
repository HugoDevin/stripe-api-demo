# Stripe Spring Boot + Vue3 Demo

此專案支援 **Vue3 與 Thymeleaf 共存**，並已加上模組化分層，避免 API Controller 與頁面 Controller 混在一起。

## 模組化分層
- `controller.api`：對外 REST API（`/api/**`）
- `controller.web`：Thymeleaf 頁面路由（`/web/**`）
- `application`：用例流程協調（`CheckoutApplicationService`）
- `domain.order`：訂單領域邏輯（`OrderService`）
- `service` / `repository`：外部整合與資料來源（Stripe、商品目錄等）

## 路由規劃
- `/`：導向 `/web`
- `/web/**`：Thymeleaf 頁面
- `/api/**`：JSON API（供 Vue3 使用）

## Backend 啟動
```bash
mvn spring-boot:run
```

必要環境變數：
- `STRIPE_SECRET_KEY`
- `STRIPE_PUBLISHABLE_KEY`

可選環境變數：
- `FRONTEND_ORIGIN`（預設 `http://localhost:5173`）
- `SPRING_CACHE_TYPE`（`redis` / `simple` / `none`）


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
- `POST /api/checkout`
- `POST /api/orders/{orderId}/complete`
- `GET /api/orders`
