
# BUILD

FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package

# RUN

FROM eclipse-temurin:21-jdk-noble
WORKDIR /app
COPY --from=builder /app/TelegramBot/target/*.jar TelegramBot.jar
ENTRYPOINT ["java", "-jar", "TelegramBot.jar"]
