# === Build stage ===
FROM maven:3.9.9-amazoncorretto-21-alpine AS builder

LABEL maintainer="Andriawan <irwan@andriawan.com>"

WORKDIR /build
COPY pom.xml ./
RUN mvn dependency:go-offline

COPY src ./src
COPY .git ./.git
RUN mvn package -DskipTests
RUN rm -rf .git
RUN mv target/*.jar app.jar

# === Runtime stage (distroless) ===
FROM eclipse-temurin:21-jre-alpine
COPY --from=builder /build/app.jar app.jar
# No shell, no user switching needed. Runs as non-root by default.
CMD ["java", "-jar", "/app.jar"]

