# Stage 1: Build application using Maven and JDK 17
FROM maven:3.9.4-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Create a minimal runtime image
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY --from=builder /app/target/notification*.jar app.jar

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
