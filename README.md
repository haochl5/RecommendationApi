# Recommendation API
1
A Spring Boot application with JWT-based authentication system and AWS RDS MySQL database integration.

## Features

- **Authentication System**: JWT-based login/logout endpoints
- **Security**: Spring Security with custom authentication provider
- **Database**: AWS RDS MySQL with JPA/Hibernate
- **API Endpoints**: Public and protected REST endpoints
- **CORS Support**: Cross-origin request handling
- **Password Encryption**: BCrypt password hashing

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- AWS RDS MySQL instance (or local MySQL for development)

### Running the Application

#### Development (Local MySQL)
```bash
# Clone the repository
git clone <repository-url>
cd RecommendationApi

# Set environment variables (optional, uses defaults)
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=recommendation_db
export DB_USERNAME=root
export DB_PASSWORD=your_password

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

#### Production (AWS RDS)
```bash
# Set AWS RDS environment variables
export DB_HOST=your-rds-endpoint.region.rds.amazonaws.com
export DB_PORT=3306
export DB_NAME=recommendation_db
export DB_USERNAME=your_db_username
export DB_PASSWORD=your_db_password
export JWT_SECRET=your-super-secure-jwt-secret

# Run with production profile
mvn spring-boot:run -Dspring.profiles.active=aws
```

The application will start on `http://localhost:8080`

### Demo User
- **Username**: `user123`
- **Password**: `securepass`

## AWS RDS Setup

### 1. Create RDS Instance
1. Go to AWS RDS Console
2. Create database → MySQL
3. Choose production template
4. Set database name: `recommendation_db`
5. Configure username/password
6. Choose VPC and security groups

### 2. Security Group Configuration
- Allow inbound MySQL (3306) from your EC2 instances
- Or allow from your development machine for testing

### 3. Initialize Database
```bash
# Connect to your RDS instance
mysql -h your-rds-endpoint -u username -p

# Run the initialization script
source src/main/resources/db/init.sql
```

### 4. Environment Variables
Set these in your EC2 instance or deployment environment:
```bash
DB_HOST=your-rds-endpoint.region.rds.amazonaws.com
DB_PORT=3306
DB_NAME=recommendation_db
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
JWT_SECRET=your-super-secure-jwt-secret
```

## API Documentation

For detailed API documentation, see [AUTHENTICATION_API.md](AUTHENTICATION_API.md)

## Architecture

- **Controllers**: REST endpoints for authentication and testing
- **Services**: Business logic for user management
- **Repositories**: Data access layer with JPA
- **Security**: JWT-based authentication with Spring Security
- **Models**: Data transfer objects and entity classes
- **Configuration**: Security and database configuration

## Technology Stack

- **Backend**: Spring Boot 3.2.0, Spring Security, Spring Data JPA
- **Database**: AWS RDS MySQL 8.0
- **Authentication**: JWT (JSON Web Tokens)
- **Build Tool**: Maven
- **Java Version**: 17
- **Cloud**: AWS RDS, EC2 ready
