-- Database initialization script for AWS RDS MySQL
-- Run this script after creating your RDS instance

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS recommendation_db;
USE recommendation_db;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_active (is_active)
);

-- Insert demo user (password: securepass - BCrypt encoded)
INSERT INTO users (username, email, password, created_at, updated_at, is_active) 
VALUES ('user123', 'user123@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', NOW(), NOW(), TRUE)
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- Create additional indexes for performance
CREATE INDEX idx_created_at ON users(created_at);
CREATE INDEX idx_updated_at ON users(updated_at);

-- Create items table
CREATE TABLE IF NOT EXISTS items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2),
    image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_item_id (item_id),
    INDEX idx_category (category),
    INDEX idx_price (price),
    INDEX idx_created_at (created_at)
);

-- Create user_clusters table
CREATE TABLE IF NOT EXISTS user_clusters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    cluster_id INT NOT NULL,
    purchased_items JSON,
    total_order_amount DECIMAL(10,2) DEFAULT 0.00,
    region INT DEFAULT 1,
    purchase_type_vector JSON,
    feature_vector JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY unique_user_cluster (user_id),
    INDEX idx_user_id (user_id),
    INDEX idx_cluster_id (cluster_id),
    INDEX idx_region (region)
);

-- Create item_clusters table
CREATE TABLE IF NOT EXISTS item_clusters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id VARCHAR(50) NOT NULL,
    cluster_id INT NOT NULL,
    description_vector JSON,
    category_encoded INT NOT NULL,
    price_normalized DECIMAL(10,4) DEFAULT 0.0000,
    rating DECIMAL(3,2) DEFAULT 0.00,
    views BIGINT DEFAULT 0,
    feature_vector JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY unique_item_cluster (item_id),
    INDEX idx_item_id (item_id),
    INDEX idx_cluster_id (cluster_id),
    INDEX idx_category_encoded (category_encoded),
    INDEX idx_rating (rating),
    INDEX idx_views (views)
);

-- Insert sample items
INSERT INTO items (item_id, name, category, description, price, image_url, created_at, updated_at) VALUES
('ITEM001', 'Wireless Bluetooth Headphones', 'electronics', 'High-quality wireless headphones with noise cancellation and 30-hour battery life', 199.99, 'https://example.com/headphones.jpg', NOW(), NOW()),
('ITEM002', 'Cotton T-Shirt', 'clothing', 'Comfortable 100% cotton t-shirt available in multiple colors', 29.99, 'https://example.com/tshirt.jpg', NOW(), NOW()),
('ITEM003', 'Programming Book', 'books', 'Complete guide to Java programming with practical examples', 49.99, 'https://example.com/book.jpg', NOW(), NOW()),
('ITEM004', 'Coffee Maker', 'home', 'Automatic drip coffee maker with programmable timer', 89.99, 'https://example.com/coffeemaker.jpg', NOW(), NOW()),
('ITEM005', 'Smartphone', 'electronics', 'Latest smartphone with advanced camera and long battery life', 699.99, 'https://example.com/smartphone.jpg', NOW(), NOW()),
('ITEM006', 'Jeans', 'clothing', 'Classic blue jeans with comfortable fit', 79.99, 'https://example.com/jeans.jpg', NOW(), NOW()),
('ITEM007', 'Cookbook', 'books', 'Collection of healthy recipes for everyday cooking', 24.99, 'https://example.com/cookbook.jpg', NOW(), NOW()),
('ITEM008', 'Desk Lamp', 'home', 'LED desk lamp with adjustable brightness and color temperature', 45.99, 'https://example.com/desklamp.jpg', NOW(), NOW()),
('ITEM009', 'Laptop', 'electronics', 'High-performance laptop for work and gaming', 1299.99, 'https://example.com/laptop.jpg', NOW(), NOW()),
('ITEM010', 'Sweater', 'clothing', 'Warm wool sweater perfect for winter', 59.99, 'https://example.com/sweater.jpg', NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- Show table structures
DESCRIBE users;
DESCRIBE items;
DESCRIBE user_clusters;
DESCRIBE item_clusters;

-- Show demo data
SELECT id, username, email, created_at, is_active FROM users WHERE username = 'user123';
SELECT item_id, name, category, price FROM items LIMIT 5;
