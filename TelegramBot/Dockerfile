
# BUILD

FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package

# RUN

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=builder /app/TelegramBot/target/*.jar TelegramBot.jar
ENTRYPOINT ["java", "-jar", "TelegramBot.jar"]
