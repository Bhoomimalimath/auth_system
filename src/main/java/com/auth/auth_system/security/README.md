# Security Layer

## 🎯 Purpose
The Security layer acts as a strict middleware that intercepts all incoming HTTP requests before they reach the Controllers. It enforces Authentication (who are you?) and Authorization (what can you do?).

## ⚙️ Responsibilities
- Intercepting HTTP requests and validating `Authorization` headers.
- Establishing the `SecurityContext` for the current thread to be used down the line by Controllers or Services.
- Enforcing Role-Based Access Control (RBAC) URL matching rules.
- Handling unauthenticated anomalies globally.

## 📄 Key Files

### `SecurityConfig.java`
The central nervous system of the application's security posture.
- Defines the `SecurityFilterChain` bean.
- Disables CSRF (appropriate for stateless REST APIs).
- Sets `SessionCreationPolicy.STATELESS` so no traditional HTTP Sessions are created.
- Registers route-specific authorizations:
  - `/auth/**` -> `permitAll()`
  - `/admin/**` -> `hasRole("ADMIN")`
  - `/user/**` -> `hasAnyRole("USER", "ADMIN")`

### `JwtAuthenticationFilter.java`
A custom `OncePerRequestFilter` executed on every API call.
- Extracts the Bearer token from the `Authorization` header.
- Delegates to `TokenService` to validate the signature and expiry.
- Extracts the roles from the claims and populates Spring's `SecurityContextHolder` with a `UsernamePasswordAuthenticationToken`.

### `AuthEntryPoint.java`
An implementation of `AuthenticationEntryPoint`.
- Intercepts requests that fail authentication (e.g., missing or invalid JWT).
- Overrides Spring Security's default redirect behavior, returning a clean, JSON-formatted `401 Unauthorized` response to the client.
