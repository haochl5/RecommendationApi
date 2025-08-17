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

-- Show table structure
DESCRIBE users;

-- Show demo user
SELECT id, username, email, created_at, is_active FROM users WHERE username = 'user123';
