-- 1. Create Tables
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    signup_date DATE
);

CREATE TABLE products (
    product_id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    category VARCHAR(50),
    price DECIMAL(10, 2)
);

CREATE TABLE orders (
    order_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id),
    order_date DATE,
    total_amount DECIMAL(10, 2)
);

CREATE TABLE order_items (
    order_item_id SERIAL PRIMARY KEY,
    order_id INT REFERENCES orders(order_id),
    product_id INT REFERENCES products(product_id),
    quantity INT
);

-- 2. Insert Dummy Data
INSERT INTO users (name, email, signup_date) VALUES 
('Alice Smith', 'alice@example.com', '2023-01-15'),
('Bob Johnson', 'bob@example.com', '2023-03-22'),
('Charlie Brown', 'charlie@example.com', '2023-06-10');

INSERT INTO products (name, category, price) VALUES 
('Laptop', 'Electronics', 1200.00),
('Desk Chair', 'Furniture', 150.00),
('Coffee Mug', 'Kitchen', 15.00);

INSERT INTO orders (user_id, order_date, total_amount) VALUES 
(1, '2023-10-01', 1200.00),
(2, '2023-10-05', 165.00),
(1, '2023-11-12', 15.00);

INSERT INTO order_items (order_id, product_id, quantity) VALUES 
(1, 1, 1),   -- Order 1 has 1 Laptop
(2, 2, 1),   -- Order 2 has 1 Desk Chair
(2, 3, 1),   -- Order 2 has 1 Coffee Mug
(3, 3, 2);   -- Order 3 has 2 Coffee Mugs