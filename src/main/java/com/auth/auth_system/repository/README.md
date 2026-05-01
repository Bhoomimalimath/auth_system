# Repository Layer

## 🎯 Purpose
The Repository layer is responsible for data access and persistence. It acts as an abstraction over the underlying SQLite database, providing an object-oriented interface for database operations.

## ⚙️ Responsibilities
- Executing CRUD (Create, Read, Update, Delete) operations on database entities.
- Translating Java method calls into SQL queries.
- Managing data pagination and sorting (if applicable).

## 📄 Key Files

### `UserRepository.java`
An interface extending Spring Data JPA's `JpaRepository`.
- **`findByUsername(String username)`**: A derived query method automatically implemented by Spring Data to fetch a `User` entity by their unique username.
- Inherits standard methods like `save()`, `findAll()`, and `deleteById()`, eliminating the need for boilerplate JDBC code.
