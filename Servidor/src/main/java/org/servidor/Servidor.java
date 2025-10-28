package org.servidor;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Collections;

@EnableScheduling
@EnableMongoRepositories(basePackages = "org.repositories")
@EnableRetry
@ComponentScan(basePackages = {"org.controllers", "org.services", "org.repositories","org.DTOs", "org.model","org.utils", "org.middleware"})
@SpringBootApplication
public class Servidor {
    public static void main(String[] args) {
        System.out.println("Servidor iniciado");
        SpringApplication app = new SpringApplication(Servidor.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.setDefaultProperties(Collections.singletonMap("logging.level.root", "WARN"));
        app.run();
        System.out.println("Springboot iniciado");
    }
}