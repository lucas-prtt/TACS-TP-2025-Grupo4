FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/Servidor-1.0.jar Servidor.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "Servidor.jar"]
