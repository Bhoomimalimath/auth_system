# Config Layer

## 🎯 Purpose
The Configuration layer handles the initialization and setup of external infrastructure components required by the application. It ensures that beans are correctly instantiated and provided to the Spring Application Context.

## ⚙️ Responsibilities
- Defining Spring `@Bean` objects that need customized setup.
- Configuring serializers/deserializers for data persistence.
- Integrating third-party infrastructure.

## 📄 Key Files

### `RedisConfig.java`
Responsible for bridging the Spring Boot application with the external Redis server.
- Sets up the `RedisTemplate<String, Object>` bean.
- **Serialization:** Applies a `StringRedisSerializer` to the keys ensuring human-readable, predictable keys in Redis.
- Applies a `GenericJackson2JsonRedisSerializer` to the values, ensuring complex Java objects (like `QRSession`) are stored as parsable JSON rather than serialized Java bytecode.
