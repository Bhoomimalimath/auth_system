# Model Layer

## 🎯 Purpose
The Model layer defines the data structures, database schemas, and data transfer objects utilized throughout the application. It acts as the single source of truth for application state structure.

## ⚙️ Responsibilities
- Defining JPA Entities that map directly to SQLite tables.
- Defining internal objects (like Redis session wrappers).
- Encapsulating Role and Permission hierarchies.

## 📄 Key Files

### `User.java`
The primary `@Entity` mapped to the underlying database table.
- Stores identity information (`username`).
- Stores credentials (`password` - highly secured, never returned in API payloads).
- Stores authorization state (`role`).

### `QRSession.java`
An internal model implementing `Serializable`.
- Represents the transient state of a cross-device login attempt.
- Stored as a JSON object inside Redis.
- Tracks `status`, creation timestamp, and the approving `username`.

### `Role.java` & `RolePermissions.java` (if applicable)
- Enumerations and static definitions that strictly type the available roles (`USER`, `ADMIN`) to prevent string-typing errors throughout the application.
