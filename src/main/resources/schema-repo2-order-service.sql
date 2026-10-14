-- ============================================================================
-- REPO 2 SPECIFIC TABLES SCHEMA (Used ONLY by repo2-order-service)
-- Tables: customer_orders, order_items
-- Database: shared_app_db
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Repo 2 Specific Table 1: customer_orders
-- Order headers; references common table 'app_users(id)'
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS customer_orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    order_number VARCHAR(100) NOT NULL UNIQUE,
    total_amount NUMERIC(12, 2) NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES app_users(id)
);

-- ----------------------------------------------------------------------------
-- Repo 2 Specific Table 2: order_items
-- Order line items; references 'customer_orders(id)'
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_sku VARCHAR(100) NOT NULL,
    product_name VARCHAR(200),
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(12, 2) NOT NULL,
    CONSTRAINT fk_item_order FOREIGN KEY (order_id) REFERENCES customer_orders(id) ON DELETE CASCADE
);

-- ----------------------------------------------------------------------------
-- Sample Seed Data for Repo 2 Specific Tables
-- ----------------------------------------------------------------------------
INSERT INTO customer_orders (id, user_id, order_number, total_amount, status)
VALUES (1, 1, 'ORD-1001', 1249.98, 'CONFIRMED')
ON CONFLICT (id) DO NOTHING;

INSERT INTO order_items (id, order_id, product_sku, product_name, quantity, unit_price)
VALUES 
(1, 1, 'IPHONE-15', 'iPhone 15 128GB', 1, 1199.99),
(2, 1, 'CASE-01', 'Silicone Case', 1, 49.99)
ON CONFLICT (id) DO NOTHING;
