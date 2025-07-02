# Springboot Best Practice

This is a minimal Spring Boot application built with standard best practices. It includes JWT bearer token authentication via Spring Security, supports auto-reloading .env configuration, and provides clean, self-documented APIs using Swagger/OpenAPI. Designed as a lightweight foundation for scalable, secure RESTful services.

## Coverage Status

[![codecov](https://codecov.io/gh/andriawan/springboot-best-practice/graph/badge.svg?token=4QCLOGCOXI)](https://codecov.io/gh/andriawan/springboot-best-practice)

## Prerequisites

This project uses JWTs signed with **asymmetric encryption** (e.g., RSA).  
You need to generate a **public/private key pair** and place them in the default dir

```bash
src/main/resources/certs/
```

or you can custom the dir through application.properties

```bash
# JWT
rsa.key.private=${RSA_KEY_PRIVATE:classpath:certs/private_key.pem}
rsa.key.public=${RSA_KEY_PUBLIC:classpath:certs/public_key.pem}
```

### Required Files

- 🔐 `private_key.pem` — used to **sign** the JWT
- 🔓 `public_key.pem` — used to **verify** the JWT

### 🔧 Generate Keys with OpenSSL

If you don't already have the keys, you can generate them using the following commands:

```bash
# Generate private key (2048-bit RSA)
openssl genrsa -out private.key 2048

# Extract the corresponding public key
openssl rsa -in private.key -pubout -out public.key
```

## Features

### ✅ JWT Authentication using Spring Security And Oauth2 Resource Server

Implements stateless authentication with JSON Web Tokens for robust and secure REST API access control. By fully leveraging Spring Security and the OAuth2 Resource Server capabilities, this approach minimizes complexity and avoids reinventing boilerplate code—ensuring a clean, maintainable, and production-ready security setup.

### ✅ Token issuance follows RFC 7617 using HTTP Basic Authentication

Implements token issuance by authenticating clients via HTTP Basic Authentication as defined in RFC 7617. This standard method securely transmits client credentials to the token endpoint, ensuring trusted clients can obtain tokens safely and efficiently.

### ✅ Secure refresh token handling — blacklists tokens after use or on logout  

Implements refresh token revocation by blacklisting used or explicitly revoked tokens to prevent reuse and enhance security.

### ✅ Environment variable mapping via `.env` to Spring properties  

Supports reading configuration from a `.env` file and mapping it to Spring's `${...}` placeholders—ideal for managing environment-specific settings. this can be done with dependency [Spring dot-env](https://github.com/paulschwarz/spring-dotenv)

### ✅ Swagger/OpenAPI docs for clean, self-documented APIs  

Generates interactive API documentation automatically with Swagger UI, making endpoints easy to explore and test.

### ✅ Maven Spotless for automatic code formatting  

Integrates Maven Spotless plugin to enforce consistent code style and automatically format your code to standard conventions, improving readability and maintainability.

### ✅ Git commit metadata integration  

Uses `io.github.git-commit-id` dependency to generate `git.properties` at build time, enabling the app to expose Git commit info for version tracking and diagnostics.

### ✅ Database migration with Flyway  

Includes Flyway dependency with base configuration, initial SQL migration scripts, and Docker Compose setup for seamless database version control and automated schema updates.

### ✅ Rate Limiting using JCache and Bucket4j — configurable via properties

Implements API rate limiting using [Bucket4j](https://github.com/MarcGiffing/bucket4j-spring-boot-starter) backed by JCache (e.g., Ehcache). Configuration is externalized via `application.properties` for easy control of rate policies, such as capacity, refill rate, and limit per IP or route—ensuring fair use and protection against abuse.

```
# first make sure you config cache. bucket4j need cache for storing limiter state
spring.cache.type=${SPRING_CACHE_TYPE:jcache}
spring.cache.jcache.config=${SPRING_CACHE_JCACHE_CONFIG:classpath:ehcache.xml}

# RATE LIMIT. these are minimal setup. you can customize based on your usecase
bucket4j.enabled=${BUCKET4J_ENABLED:false}
bucket4j.filters[0].cache-name=${BUCKET4J_CACHE_NAME:buckets}
bucket4j.filters[0].url=${BUCKET4J_URL:.*}
bucket4j.filters[0].rate-limits[0].bandwidths[0].capacity=${BUCKET4J_CAPACITY:10}
bucket4j.filters[0].rate-limits[0].bandwidths[0].time=${BUCKET4J_TIME_PERIOD:1}
bucket4j.filters[0].rate-limits[0].bandwidths[0].unit=${BUCKET4J_TIME_UNIT:minutes}
bucket4j.filters[0].http-response-headers.include-remaining=${BUCKET4J_HTTP_RESPONSE_HEADERS_INCLUDE_REMAINING:true}
bucket4j.filters[0].http-response-headers.include-limit=${BUCKET4J_HTTP_RESPONSE_HEADERS_INCLUDE_LIMIT:true}
bucket4j.filters[0].http-response-headers.include-reset=${BUCKET4J_HTTP_RESPONSE_HEADERS_INCLUDE_RESET:true}

```

### ✅ Out-of-the-box CORS setup via application properties     

CORS configuration can be easily managed through application.properties. Just enable or disable the config

```
cors.enabled=${CORS_ENABLED:false}
cors.origin.allowed=${CORS_ORIGIN_ALLOWED:*}
cors.header.allowed=${CORS_HEADER_ALLOWED:*}
cors.method.allowed=${CORS_METHOD_ALLOWED:*}

```

### ✅ Integration testing with Testcontainers  

Supports full integration tests using Testcontainers, including auto-configuration with `@ServiceConnection`. This allows isolated, reproducible testing against real services like PostgreSQL or Redis in disposable Docker containers.

### ✅ Dependency Updates

Regularly updates project dependencies to ensure security, performance, and compatibility with the latest versions.

Perfect as a starter template for any Java backend project!
Check it out and feel free to ⭐ or fork!