CREATE TABLE products(
  sku VARCHAR(64) PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  price BIGINT NOT NULL,
  currency VARCHAR(8) NOT NULL,
  active BOOLEAN NOT NULL,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE inventory(
  sku VARCHAR(64) PRIMARY KEY REFERENCES products(sku),
  available_qty BIGINT NOT NULL CHECK (available_qty >= 0),
  reserved_qty BIGINT NOT NULL,
  version BIGINT NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE inventory_reservations(
  id VARCHAR(64) PRIMARY KEY,
  order_id VARCHAR(64) NOT NULL,
  sku VARCHAR(64) NOT NULL,
  qty BIGINT NOT NULL,
  status VARCHAR(32) NOT NULL,
  expires_at TIMESTAMPTZ NOT NULL,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL,
  UNIQUE(order_id, sku)
);

CREATE TABLE orders(
  id VARCHAR(64) PRIMARY KEY,
  customer_email VARCHAR(255) NOT NULL,
  status VARCHAR(32) NOT NULL,
  total_amount BIGINT NOT NULL,
  currency VARCHAR(8) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE order_items(
  id VARCHAR(64) PRIMARY KEY,
  order_id VARCHAR(64) NOT NULL REFERENCES orders(id),
  sku VARCHAR(64) NOT NULL,
  name VARCHAR(255) NOT NULL,
  unit_price BIGINT NOT NULL,
  qty BIGINT NOT NULL,
  line_total BIGINT NOT NULL
);

CREATE TABLE payments(
  id VARCHAR(64) PRIMARY KEY,
  order_id VARCHAR(64) NOT NULL UNIQUE REFERENCES orders(id),
  provider VARCHAR(32) NOT NULL,
  provider_intent_id VARCHAR(128) NOT NULL UNIQUE,
  status VARCHAR(32) NOT NULL,
  amount BIGINT NOT NULL,
  currency VARCHAR(8) NOT NULL,
  client_secret TEXT,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE webhook_events(
  id VARCHAR(64) PRIMARY KEY,
  provider_event_id VARCHAR(128) NOT NULL UNIQUE,
  type VARCHAR(128) NOT NULL,
  payload_json TEXT NOT NULL,
  received_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE outbox_events(
  id VARCHAR(64) PRIMARY KEY,
  event_type VARCHAR(128) NOT NULL,
  aggregate_id VARCHAR(64) NOT NULL,
  payload_json TEXT NOT NULL,
  status VARCHAR(16) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL,
  sent_at TIMESTAMPTZ
);

CREATE TABLE receipts(
  id VARCHAR(64) PRIMARY KEY,
  order_id VARCHAR(64) NOT NULL UNIQUE,
  receipt_no VARCHAR(64) NOT NULL UNIQUE,
  issued_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE fulfillments(
  id VARCHAR(64) PRIMARY KEY,
  order_id VARCHAR(64) NOT NULL UNIQUE,
  status VARCHAR(32) NOT NULL,
  activated_at TIMESTAMPTZ,
  details_json TEXT
);

CREATE TABLE notifications(
  id VARCHAR(64) PRIMARY KEY,
  order_id VARCHAR(64) NOT NULL,
  channel VARCHAR(32) NOT NULL,
  to_addr VARCHAR(255) NOT NULL,
  subject VARCHAR(255) NOT NULL,
  body TEXT NOT NULL,
  status VARCHAR(32) NOT NULL,
  sent_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE accounting_entries(
  id VARCHAR(64) PRIMARY KEY,
  order_id VARCHAR(64) NOT NULL UNIQUE,
  debit_account VARCHAR(64) NOT NULL,
  credit_account VARCHAR(64) NOT NULL,
  amount BIGINT NOT NULL,
  currency VARCHAR(8) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE sales_daily_fact(
  date DATE PRIMARY KEY,
  total_amount BIGINT NOT NULL,
  order_count BIGINT NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE processed_events(
  id VARCHAR(64) PRIMARY KEY,
  event_id VARCHAR(128) NOT NULL,
  consumer_name VARCHAR(64) NOT NULL,
  processed_at TIMESTAMPTZ NOT NULL,
  UNIQUE(event_id, consumer_name)
);
