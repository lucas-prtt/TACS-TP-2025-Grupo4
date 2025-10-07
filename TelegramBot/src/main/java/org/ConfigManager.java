package org;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
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
        for (String key : properties.stringPropertyNames()) {
            String override = System.getProperty(key);
            if (override != null) {
                properties.setProperty(key, override);
            }
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
    public Optional<String> getOptional(String key) {
        try{
            return Optional.of(get(key));
        }
        catch (Exception e){
            return Optional.empty();
        }
    }
    public Integer getInteger(String key){
        return Integer.parseInt(get(key));
    }
    public Optional<Integer> getOptionalInteger(String key){
        try{
            return Optional.of(getInteger(key));
        }
        catch (Exception e){
            return Optional.empty();
        }
    }
}