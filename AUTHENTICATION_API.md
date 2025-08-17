# Authentication API Documentation

## Overview
This document describes the authentication endpoints for the Recommendation API. The API uses JWT (JSON Web Tokens) for stateless authentication.

## Base URL
```
http://localhost:8080
```

## Authentication Endpoints

### 1. User Login
**POST** `/auth/login`

Authenticates a user and returns a JWT token.

**Request Body:**
```json
{
    "username": "user123",
    "password": "securepass"
}
```

**Response (Success - 200):**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "expiresIn": 3600,
    "message": "Login successful"
}
```

**Response (Error - 401):**
```json
{
    "message": "Invalid username or password"
}
```

### 2. User Logout
**POST** `/auth/logout`

Invalidates the user session by clearing the security context.

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (Success - 200):**
```json
{
    "message": "Successfully logged out",
    "success": true
}
```

**Response (Error - 400):**
```json
{
    "message": "No valid token provided",
    "success": false
}
```

### 3. Token Validation
**GET** `/auth/validate`

Validates a JWT token and returns user information.

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (Success - 200):**
```
Token is valid for user: user123
```

**Response (Error - 401):**
```
Invalid token
```

## Protected Endpoints

### Test Protected Endpoint
**GET** `/api/protected`

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response:**
```
This is a protected endpoint. You are authenticated!
```

### User Information
**GET** `/api/user-info`

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response:**
```
User information endpoint - requires authentication
```

## Public Endpoints

### Home
**GET** `/`
**Response:** Welcome to My Spring Boot Application!

### Hello World
**GET** `/hello`
**Response:** Hello, World!

### Hello with Name
**GET** `/hello/{name}`
**Response:** Hello, {name}!

## Demo User Credentials

For testing purposes, a demo user is pre-configured:

- **Username:** `user123`
- **Password:** `securepass`
- **Email:** `user123@example.com`

## JWT Token Usage

1. **Login** to get a JWT token
2. **Include the token** in the `Authorization` header for protected endpoints:
   ```
   Authorization: Bearer <your_jwt_token>
   ```
3. **Token expires** after 3600 seconds (1 hour)

## Security Features

- **JWT-based authentication** for stateless sessions
- **Password encryption** using BCrypt
- **CORS enabled** for cross-origin requests
- **CSRF protection disabled** (stateless API)
- **Session management** set to STATELESS

## Error Handling

- **400 Bad Request:** Invalid request format or missing token
- **401 Unauthorized:** Invalid credentials or expired token
- **500 Internal Server Error:** Server-side errors

## Testing with cURL

### Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user123","password":"securepass"}'
```

### Access Protected Endpoint
```bash
curl -X GET http://localhost:8080/api/protected \
  -H "Authorization: Bearer <token_from_login>"
```

### Logout
```bash
curl -X POST http://localhost:8080/auth/logout \
  -H "Authorization: Bearer <token_from_login>"
```

## Configuration

JWT configuration can be modified in `application.properties`:

```properties
jwt.secret=your-super-secret-jwt-key-here
jwt.expiration=3600
```

**Note:** In production, use a strong, randomly generated secret key and store it securely (e.g., environment variables, AWS Secrets Manager).
