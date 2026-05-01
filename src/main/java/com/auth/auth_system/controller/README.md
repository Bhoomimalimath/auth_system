# Controller Layer

## 🎯 Purpose
The Controller layer acts as the API boundary for the application. It is responsible for handling incoming HTTP requests, enforcing input validation, and delegating business logic to the Service layer.

## ⚙️ Responsibilities
- **HTTP Request Mapping:** Maps URIs to specific Java methods using `@RestController` and `@RequestMapping`.
- **Input Validation:** Utilizes Jakarta Validation constraints (`@Valid`, `@NotBlank`, etc.) defined in the DTOs to sanitize incoming payloads before they reach the business logic.
- **Response Standardization:** Wraps all outgoing data (and exceptions, via global handlers) into a unified JSON structure (`ApiResponse`).

## 📄 Key Files

### `AuthController.java`
Manages all public-facing authentication endpoints.
- `POST /auth/register`: Accepts a `RegisterRequest` and delegates to `AuthService`.
- `POST /auth/login`: Accepts a `LoginRequest` and returns a generated JWT.

### `QRController.java`
Orchestrates the cross-device QR authentication flow.
- `POST /auth/qr/init`: Starts a session and returns a Base64 QR code.
- `POST /auth/qr/approve`: Secure endpoint for authenticated devices to verify a session.
- `GET /auth/qr/status`: Polling endpoint for the unauthenticated device to check status and receive its JWT.

### `UserController.java` & `AdminController.java`
Demonstrate RBAC-secured endpoints.
- Accessible only to clients providing a valid JWT with the required roles.
