# Stage 1: Build the application using Maven
FROM maven:3.9.6-eclipse-temurin-17 as builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Use JDK 17 runtime image to run the app
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8020

ENTRYPOINT ["java", "-jar", "app.jar"]
