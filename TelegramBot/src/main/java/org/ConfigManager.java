package org;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {

    private static ConfigManager instance;
    private Properties properties;

    private ConfigManager() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("No se encontró el archivo config.properties en el classpath.");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo de configuración", e);
        }
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public String get(String key) {
        return properties.getProperty(key);
    }
}