package org.servidor;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

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