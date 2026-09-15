# === Build stage ===
FROM maven:3.9.12-amazoncorretto-25-alpine AS builder

LABEL maintainer="Andriawan <irwan@andriawan.com>"

WORKDIR /build
COPY pom.xml ./
COPY src ./src
COPY .git ./.git
RUN mvn package -DskipTests
RUN rm -rf .git
RUN mv target/*.jar app.jar

# === Runtime stage (distroless) ===
FROM eclipse-temurin:25-jre-alpine
COPY --from=builder /build/app.jar app.jar
CMD ["java", "-jar", "/app.jar"]

