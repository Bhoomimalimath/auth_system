# 🛡️ Spring Boot Authentication System

A production-ready, secure, and robust Authentication System built with **Java Spring Boot**. This project provides a complete foundation for user identity management, featuring JWT-based authentication, Role-Based Access Control (RBAC), SQLite persistence, and an innovative **QR-based Cross-Device Login** mechanism using Redis.

---

## ✨ Key Features
- **JWT Authentication:** Secure, stateless token-based authentication.
- **Role-Based Access Control (RBAC):** Granular access management with defined roles (`USER`, `ADMIN`).
- **Cross-Device QR Login:** Seamlessly log into an unauthenticated device by scanning a QR code with an authenticated device.
- **SQLite Persistence:** Zero-configuration embedded database for rapid deployment.
- **Redis Session Management:** Ephemeral, high-performance session tracking for QR authentication flows.

---

## 🏗️ Architecture Overview

The system adheres to a strict **4-Layer Architecture** to ensure separation of concerns and maintainability:

1. **Controller Layer (`/controller`)**: Entry point for REST APIs. Handles HTTP requests, input validation, and response mapping.
2. **Service Layer (`/service`)**: Encapsulates core business logic, including authentication rules, hashing, and QR orchestration.
3. **Repository Layer (`/repository`)**: Data access layer leveraging Spring Data JPA for SQLite interactions.
4. **Model Layer (`/model`)**: Defines JPA entities and internal data structures.

**Security Layer (`/security`)**: Operates as middleware, intercepting requests via a `JwtAuthenticationFilter` to validate tokens and enforce RBAC policies configured in `SecurityConfig`.

---

## 🛠️ Tech Stack
- **Backend Framework:** Java 17+, Spring Boot 3.x
- **Database:** SQLite
- **In-Memory Store:** Redis
- **Security:** Spring Security, JSON Web Tokens (jjwt), BCrypt
- **Build Tool:** Maven

---

## 📂 Project Structure
```text
src/main/java/com/auth/auth_system/
├── config/        # Global configurations (Redis serialization, etc.)
├── controller/    # REST API Endpoints (Auth, QR, User, Admin)
├── dto/           # Data Transfer Objects (Requests/Responses)
├── exception/     # Global Exception Handling and Advice
├── model/         # Database Entities and internal classes
├── repository/    # Spring Data JPA Interfaces
├── security/      # Security Configuration, JWT Filters, EntryPoints
└── service/       # Business Logic and Service Beans
```
*(See the README.md in each folder for detailed, file-level documentation).*

---

## 🚀 Prerequisites

To run this application locally, ensure you have the following installed:
- **Java:** JDK 17 or higher
- **Maven:** v3.6+
- **Redis:** A running Redis server (local or Docker container)

---

## ⚙️ Configuration Guide

The project relies on `src/main/resources/application.properties`. 

```properties
# SQLite Configuration
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:sqlite:auth.db
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect

# Redis Configuration (Required for QR Auth)
spring.redis.host=localhost
spring.redis.port=6379

# Application Context
server.port=8080
```
*Note: SQLite requires no manual installation. The `auth.db` file will be generated automatically upon startup.*

---

## 🏃 How to Run the Project

1. **Start Redis:**
   ```bash
   redis-server
   ```
   *(Verify Redis is running by executing `redis-cli ping` which should return `PONG`).*

2. **Start the Application:**
   ```bash
   ./mvnw spring-boot:run
   ```

### Verification:
- The terminal should indicate the app has started on port 8080.
- An `auth.db` file will appear in the root directory.
- Test the health/startup by making a `POST` request to `http://localhost:8080/auth/register`.

---

## 📖 API Overview & Usage

### 1. Register a User
- **Endpoint:** `POST /auth/register`
- **Request:**
  ```json
  {
    "username": "alex",
    "password": "securePassword123",
    "role": "USER"
  }
  ```
- **Response (200 OK):**
  ```json
  {
    "message": "Success",
    "data": { "username": "alex", "role": "USER" }
  }
  ```

### 2. Login
- **Endpoint:** `POST /auth/login`
- **Request:**
  ```json
  {
    "username": "alex",
    "password": "securePassword123"
  }
  ```
- **Response (200 OK):**
  ```json
  {
    "message": "Success",
    "data": { "token": "eyJhbGci...", "role": "USER" }
  }
  ```

### 3. QR Cross-Device Login Flow

- **INIT (Laptop)**
  - `POST /auth/qr/init`
  - Returns a `sessionId` and a Base64 encoded QR Code image.
  
- **APPROVE (Phone - Requires Auth Header)**
  - `POST /auth/qr/approve`
  - **Header:** `Authorization: Bearer <phone_jwt_token>`
  - **Request:** `{ "sessionId": "<uuid>" }`
  
- **STATUS (Laptop - Polling)**
  - `GET /auth/qr/status?sessionId=<uuid>`
  - **Response:**
    - Before approval: `{ "data": { "status": "PENDING" } }`
    - After approval: `{ "data": { "status": "VERIFIED", "token": "<new_jwt_for_laptop>" } }`

---

## 🔐 Security & Authentication Flow

### Standard Flow:
1. User provides credentials to `/auth/login`.
2. `AuthService` validates the BCrypt hash against the SQLite DB.
3. `TokenService` issues a signed JWT containing roles.
4. Subsequent requests pass the JWT in the `Authorization: Bearer <token>` header.
5. `JwtAuthenticationFilter` validates the signature and populates the `SecurityContext`.

### QR Flow:
1. **Initiation:** Unauthenticated device creates a temporary session ID in Redis (TTL: 120s).
2. **Approval:** Authenticated device scans the QR and hits `/approve`, securely linking its identity to the session.
3. **Resolution:** The polling device retrieves the verified session, receives a newly minted JWT, and the Redis session is instantly destroyed to prevent replay attacks.

### Security Highlights:
- **Zero Credential Exposure:** Passwords are never returned in responses and are securely hashed using BCrypt.
- **Stateless:** The server maintains no session state outside of the ephemeral QR flow (enforced via `SessionCreationPolicy.STATELESS`).
- **RBAC Enforcement:** Controller endpoints are strictly guarded via path-matching in `SecurityConfig`.

---

## 🚧 Limitations & Future Improvements
- **Refresh Tokens:** Implement a `/refresh` endpoint to handle long-lived user sessions securely without requiring re-authentication.
- **Token Revocation:** Introduce a Redis-based blacklist for JWTs to allow immediate session termination.
- **Rate Limiting:** Protect public endpoints (`/register`, `/login`) against brute-force attacks.
- **OAuth2 Integration:** Add support for external identity providers (Google, GitHub).
