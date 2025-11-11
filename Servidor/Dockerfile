
# BUILD

FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY .. .
RUN mvn clean package

#RUN

FROM eclipse-temurin:21-jdk-noble
WORKDIR /app
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.21.0/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar
COPY --from=builder /app/Servidor/target/*.jar Servidor.jar
EXPOSE 8080
ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-jar", "Servidor.jar"]
