
package org.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

/**
 * Utilidad para gestionar la configuración de la aplicación a través de un archivo de propiedades.
 * Permite obtener valores de configuración como String o Integer, con soporte para valores por defecto y opcionales.
 */
public class ConfigManager {

    /** Instancia única de ConfigManager (patrón Singleton). */
    private static ConfigManager instance;
    /** Propiedades cargadas desde el archivo de configuración. */
    private final Properties properties;

    /**
     * Constructor privado. Carga las propiedades desde el archivo config.properties.
     * @throws RuntimeException si no se encuentra el archivo o hay error de lectura.
     */
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

    /**
     * Obtiene la instancia única de ConfigManager.
     * @return Instancia de ConfigManager.
     */
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    /**
     * Obtiene el valor de una clave como String.
     * @param key Clave de configuración.
     * @return Valor asociado a la clave, o null si no existe.
     */
    public String get(String key) {
        return properties.getProperty(key);
    }

    /**
     * Obtiene el valor de una clave como Optional<String>.
     * @param key Clave de configuración.
     * @return Optional con el valor, o vacío si no existe o hay error.
     */
    public Optional<String> getOptional(String key) {
        try{
            return Optional.of(get(key));
        }
        catch (Exception e){
            return Optional.empty();
        }
    }

    /**
     * Obtiene el valor de una clave como Integer.
     * @param key Clave de configuración.
     * @return Valor Integer asociado a la clave.
     * @throws NumberFormatException si el valor no es un número válido.
     */
    public Integer getInteger(String key){
        return Integer.parseInt(get(key));
    }

    /**
     * Obtiene el valor de una clave como Optional<Integer>.
     * @param key Clave de configuración.
     * @return Optional con el valor Integer, o vacío si no existe o hay error.
     */
    public Optional<Integer> getOptionalInteger(String key){
        try{
            return Optional.of(getInteger(key));
        }
        catch (Exception e){
            return Optional.empty();
        }
    }

    /**
     * Obtiene el valor Integer de una clave, o retorna el valor por defecto si no existe o hay error.
     * @param key Clave de configuración.
     * @param defaultValue Valor por defecto si la clave no existe o hay error.
     * @return Valor Integer asociado a la clave, o el valor por defecto.
     */
    public Integer getIntegerOrElse(String key, Integer defaultValue){
        return getOptionalInteger(key).orElse(defaultValue);
    }

    /**
     * Obtiene el valor String de una clave, o retorna el valor por defecto si no existe o hay error.
     * @param key Clave de configuración.
     * @param defaultValue Valor por defecto si la clave no existe o hay error.
     * @return Valor String asociado a la clave, o el valor por defecto.
     */
    public String getOrElse(String key, String defaultValue) {
        return getOptional(key).orElse(defaultValue);
    }

}