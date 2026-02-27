INSERT INTO catalog_products (name, price, active) VALUES ('Book', 1000, TRUE);
INSERT INTO catalog_products (name, price, active) VALUES ('Pen', 500, TRUE);
INSERT INTO catalog_products (name, price, active) VALUES ('Notebook', 750, TRUE);

INSERT INTO orders (id, product, amount, currency, status, payment_intent_id, customer_id, created_at)
VALUES ('dev-order-1', 'Book', 1000, 'USD', 'succeeded', 'pi_dev_001', 'alice@example.com', CURRENT_TIMESTAMP());
INSERT INTO orders (id, product, amount, currency, status, payment_intent_id, customer_id, created_at)
VALUES ('dev-order-2', 'Pen', 500, 'USD', 'pending', 'pi_dev_002', 'bob@example.com', CURRENT_TIMESTAMP());
