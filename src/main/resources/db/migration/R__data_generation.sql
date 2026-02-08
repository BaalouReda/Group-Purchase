-- Repeatable migration for test data generation
-- Clean existing data (in order respecting foreign keys)
DELETE FROM t_price_tier;
DELETE FROM t_product;
DELETE FROM t_customer;
DELETE FROM t_user;

-- Insert Users (password : 1234)
INSERT INTO t_user (id, email, password, role, enabled, created_at, updated_at) VALUES
    ('550e8400-e29b-41d4-a716-446655440001', 'john.doe@example.com', '$2a$10$sB4QXnJ8iio3zz0ikOK84uOYLVr7GgVfWQ9qClBOwdqUg9ON3nzH6', 'CUSTOMER', true, NOW(), NOW()),
    ('550e8400-e29b-41d4-a716-446655440002', 'jane.smith@example.com', '$2a$10$sB4QXnJ8iio3zz0ikOK84uOYLVr7GgVfWQ9qClBOwdqUg9ON3nzH6', 'CUSTOMER', true, NOW(), NOW()),
    ('550e8400-e29b-41d4-a716-446655440003', 'bob.wilson@example.com', '$2a$10$sB4QXnJ8iio3zz0ikOK84uOYLVr7GgVfWQ9qClBOwdqUg9ON3nzH6', 'CUSTOMER', true, NOW(), NOW());

-- Insert Customers
INSERT INTO t_customer (id, user_id, first_name, last_name, phone, created_at, updated_at) VALUES
    ('650e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'John', 'Doe', '+212600000001', NOW(), NOW()),
    ('650e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440002', 'Jane', 'Smith', '+212600000002', NOW(), NOW()),
    ('650e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440003', 'Bob', 'Wilson', '+212600000003', NOW(), NOW());

-- Insert Products
INSERT INTO t_product (id, name, description, base_price, active, created_at, updated_at) VALUES
    ('750e8400-e29b-41d4-a716-446655440001', 'Laptop HP ProBook 450', 'Professional laptop with Intel Core i7, 16GB RAM, 512GB SSD', 1000.00, true, NOW(), NOW()),
    ('750e8400-e29b-41d4-a716-446655440002', 'Smartphone Samsung Galaxy S24', 'Latest Samsung flagship with 256GB storage, 5G connectivity', 800.00, true, NOW(), NOW());

INSERT INTO t_price_tier (id, product_id, threshold, discount_pct, created_at, updated_at) VALUES
    ('850e8400-e29b-41d4-a716-446655440001', '750e8400-e29b-41d4-a716-446655440001', 2, 10.00, NOW(), NOW()),
    ('850e8400-e29b-41d4-a716-446655440002', '750e8400-e29b-41d4-a716-446655440001', 3, 15.00, NOW(), NOW());

INSERT INTO t_price_tier (id, product_id, threshold, discount_pct, created_at, updated_at) VALUES
    ('850e8400-e29b-41d4-a716-446655440003', '750e8400-e29b-41d4-a716-446655440002', 3, 8.00, NOW(), NOW()),
    ('850e8400-e29b-41d4-a716-446655440004', '750e8400-e29b-41d4-a716-446655440002', 7, 12.00, NOW(), NOW());
