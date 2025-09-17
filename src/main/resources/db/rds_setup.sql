-- RDS Database setup script for Recommendation API
-- Run this script on your AWS RDS MySQL instance

-- Create database if it doesn't exist (RDS usually has this already)
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

-- Insert demo user
INSERT INTO users (username, email, password, created_at, updated_at, is_active) 
VALUES ('user123', 'user123@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', NOW(), NOW(), TRUE)
ON DUPLICATE KEY UPDATE updated_at = NOW();

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

-- Insert item clusters with realistic feature vectors
INSERT INTO item_clusters (item_id, cluster_id, description_vector, category_encoded, price_normalized, rating, views, feature_vector, created_at, updated_at) VALUES
-- Electronics cluster (cluster_id = 0)
('ITEM001', 0, '{"wireless": 2, "bluetooth": 2, "headphones": 2, "noise": 1, "cancellation": 1}', 1, 0.1000, 4.2, 1250, '[0.8, 0.6, 0.7, 0.25, 0.2, 0.84, 0.125, 0.75, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]', NOW(), NOW()),
('ITEM005', 0, '{"smartphone": 1, "latest": 1, "advanced": 1, "camera": 1, "battery": 1}', 1, 0.3500, 4.5, 2100, '[0.9, 0.4, 0.6, 0.25, 0.7, 0.9, 0.21, 0.85, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]', NOW(), NOW()),
('ITEM009', 0, '{"laptop": 1, "high": 1, "performance": 2, "work": 1, "gaming": 1}', 1, 0.6500, 4.7, 3200, '[0.95, 0.5, 0.8, 0.25, 1.3, 0.94, 0.32, 0.9, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]', NOW(), NOW()),

-- Clothing cluster (cluster_id = 1)
('ITEM002', 1, '{"cotton": 2, "t-shirt": 2, "comfortable": 1, "soft": 1}', 2, 0.0150, 3.8, 850, '[0.6, 0.3, 0.4, 0.5, 0.03, 0.76, 0.085, 0.6, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]', NOW(), NOW()),
('ITEM006', 1, '{"jeans": 1, "classic": 1, "blue": 1, "comfortable": 1, "fit": 1}', 2, 0.0400, 4.1, 1100, '[0.7, 0.4, 0.5, 0.5, 0.08, 0.82, 0.11, 0.7, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]', NOW(), NOW()),
('ITEM010', 1, '{"sweater": 1, "warm": 1, "wool": 1, "winter": 1, "cozy": 1}', 2, 0.0300, 4.0, 950, '[0.65, 0.35, 0.45, 0.5, 0.06, 0.8, 0.095, 0.65, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]', NOW(), NOW()),

-- Books cluster (cluster_id = 2)
('ITEM003', 2, '{"programming": 1, "book": 1, "java": 2, "practical": 1, "examples": 1}', 3, 0.0250, 4.3, 1800, '[0.85, 0.6, 0.7, 0.75, 0.05, 0.86, 0.18, 0.8, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]', NOW(), NOW()),
('ITEM007', 2, '{"cookbook": 1, "healthy": 1, "recipes": 2, "cooking": 1, "food": 1}', 3, 0.0125, 3.9, 1200, '[0.75, 0.5, 0.6, 0.75, 0.025, 0.78, 0.12, 0.7, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]', NOW(), NOW()),

-- Home cluster (cluster_id = 3)
('ITEM004', 3, '{"coffee": 1, "maker": 1, "automatic": 1, "drip": 1, "programmable": 1}', 4, 0.0450, 4.4, 1650, '[0.8, 0.5, 0.65, 1.0, 0.09, 0.88, 0.165, 0.8, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]', NOW(), NOW()),
('ITEM008', 3, '{"desk": 1, "lamp": 1, "led": 1, "adjustable": 1, "brightness": 1}', 4, 0.0230, 4.1, 980, '[0.7, 0.4, 0.55, 1.0, 0.046, 0.82, 0.098, 0.7, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]', NOW(), NOW());

-- Insert user clusters with realistic data
INSERT INTO user_clusters (user_id, cluster_id, purchased_items, total_order_amount, region, purchase_type_vector, feature_vector, created_at, updated_at) VALUES
-- Tech users (cluster_id = 0)
('user123', 0, '["ITEM001", "ITEM005", "ITEM009"]', 2199.97, 1, '[0.6, 0.0, 0.0, 0.4]', '[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.8, 1.0, 0.3, 0.22, 0.73, 0.25, 0.4, 0.9]', NOW(), NOW()),
('techuser1', 0, '["ITEM001", "ITEM009"]', 1499.98, 1, '[0.5, 0.0, 0.0, 0.5]', '[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.7, 1.0, 0.2, 0.15, 0.75, 0.25, 0.3, 0.8]', NOW(), NOW()),
('gadgetlover', 0, '["ITEM005", "ITEM001"]', 899.98, 1, '[0.5, 0.0, 0.0, 0.5]', '[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.9, 1.0, 0.2, 0.09, 0.45, 0.25, 0.2, 0.85]', NOW(), NOW()),

-- Fashion users (cluster_id = 1)
('fashionista', 1, '["ITEM002", "ITEM006", "ITEM010"]', 169.97, 2, '[0.0, 1.0, 0.0, 0.0]', '[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.6, 1.0, 0.3, 0.017, 0.57, 0.5, 0.4, 0.7]', NOW(), NOW()),
('stylequeen', 1, '["ITEM002", "ITEM010"]', 89.98, 2, '[0.0, 1.0, 0.0, 0.0]', '[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.5, 1.0, 0.2, 0.009, 0.45, 0.5, 0.2, 0.6]', NOW(), NOW()),
('trendyuser', 1, '["ITEM006", "ITEM002"]', 109.98, 2, '[0.0, 1.0, 0.0, 0.0]', '[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.7, 1.0, 0.2, 0.011, 0.55, 0.5, 0.2, 0.8]', NOW(), NOW()),

-- Mixed users (cluster_id = 2)
('bookworm', 2, '["ITEM003", "ITEM007"]', 74.98, 3, '[0.0, 0.0, 1.0, 0.0]', '[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.8, 1.0, 0.2, 0.0075, 0.37, 0.75, 0.3, 0.9]', NOW(), NOW()),
('homeowner', 2, '["ITEM004", "ITEM008"]', 135.98, 4, '[0.0, 0.0, 0.0, 1.0]', '[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.9, 1.0, 0.2, 0.014, 0.68, 1.0, 0.3, 0.85]', NOW(), NOW()),
('student1', 2, '["ITEM003", "ITEM008"]', 94.98, 3, '[0.0, 0.0, 0.5, 0.5]', '[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.7, 1.0, 0.2, 0.0095, 0.47, 0.75, 0.2, 0.8]', NOW(), NOW());

-- Show summary
SELECT 'RDS Database setup completed successfully!' as status;
SELECT 'Tables created:' as info, 'users, items, user_clusters, item_clusters' as tables;
SELECT 'Sample data inserted:' as info, '10 items, 9 users, cluster assignments' as data;
SELECT 'Ready for testing!' as next_step;
