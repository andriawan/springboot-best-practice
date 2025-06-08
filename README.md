This is a minimal Spring Boot application built with standard best practices. It includes JWT bearer token authentication via Spring Security, supports auto-reloading .env configuration, and provides clean, self-documented APIs using Swagger/OpenAPI. Designed as a lightweight foundation for scalable, secure RESTful services.

### Features

✅ **JWT Authentication using Spring Security And Oauth2 Resource Server**  
Implements stateless authentication with JSON Web Tokens for robust and secure REST API access control. By fully leveraging Spring Security and the OAuth2 Resource Server capabilities, this approach minimizes complexity and avoids reinventing boilerplate code—ensuring a clean, maintainable, and production-ready security setup.

✅ **Token issuance follows RFC 7617 using HTTP Basic Authentication**
Implements token issuance by authenticating clients via HTTP Basic Authentication as defined in RFC 7617. This standard method securely transmits client credentials to the token endpoint, ensuring trusted clients can obtain tokens safely and efficiently.

✅ **Secure refresh token handling — blacklists tokens after use or on logout**  
Implements refresh token revocation by blacklisting used or explicitly revoked tokens to prevent reuse and enhance security.

✅ **Environment variable mapping via `.env` to Spring properties**  
Supports reading configuration from a `.env` file and mapping it to Spring's `${...}` placeholders—ideal for managing environment-specific settings. this can be done with dependency [Spring dot-env](https://github.com/paulschwarz/spring-dotenv)

✅ **Swagger/OpenAPI docs for clean, self-documented APIs**  
Generates interactive API documentation automatically with Swagger UI, making endpoints easy to explore and test.

✅ **Maven Spotless for automatic code formatting**  
Integrates Maven Spotless plugin to enforce consistent code style and automatically format your code to standard conventions, improving readability and maintainability.

✅ **Git commit metadata integration**  
Uses `io.github.git-commit-id` dependency to generate `git.properties` at build time, enabling the app to expose Git commit info for version tracking and diagnostics.

✅ **Database migration with Flyway**  
Includes Flyway dependency with base configuration, initial SQL migration scripts, and Docker Compose setup for seamless database version control and automated schema updates.


### 🛠️ In Progress

- [ ] 🔬 Writing test cases to cover all logic branches and edge scenarios

Perfect as a starter template for any Java backend project!
Check it out and feel free to ⭐ or fork!