# Service Layer

## 🎯 Purpose
The Service layer contains the core business logic of the application. It acts as the intermediary between the API (Controllers) and the Data Access Layer (Repositories), ensuring that business rules are enforced before data is persisted or retrieved.

## ⚙️ Responsibilities
- Orchestrating complex operations (e.g., User Registration involves checking for duplicates, hashing passwords, and saving).
- Managing cryptographic operations like Password Hashing and JWT generation.
- Handling ephemeral state transitions for features like QR Login using Redis.

## 📄 Key Files

### `AuthService.java`
Handles standard authentication workflows.
- **`registerUser()`**: Validates uniqueness, hashes the incoming plaintext password using BCrypt, and persists the new `User` to the repository.
- **`loginUser()`**: Retrieves the user, verifies the password hash, and delegates to `TokenService` to issue a JWT. Throws a custom `AuthException` upon failure.

### `QRSessionService.java`
Manages the lifecycle of Redis-backed cross-device authentication sessions.
- **`createSession()`**: Generates a UUID, stores a `PENDING` state in Redis (with TTL), and returns the UUID.
- **`approveSession()`**: Validates the session exists and transitions it to `VERIFIED`, attaching the approving user's identity.
- **`completeLogin()`**: Checks for a `VERIFIED` session, generates the final JWT, and securely deletes the session to prevent replay attacks.

### `TokenService.java`
Centralized utility for JSON Web Tokens.
- **`generateToken()`**: Creates a signed JWT containing the user's roles and a predefined expiration.
- **`validateToken()` / `extractClaims()`**: Parses incoming tokens and validates their signature and expiry timestamp.
