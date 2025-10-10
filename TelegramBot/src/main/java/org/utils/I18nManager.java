package org.utils;

import com.opencsv.CSVReader;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.*;

public class I18nManager {
    private static final Map<String, Map<String, String>> MESSAGES = new HashMap<>();
    private static final String DEFAULT_LANG = "es";
    static {
        loadMessages("translations.csv");
    }

    public static Set<String> getLanguageKeys(){
        return MESSAGES.get("language").keySet();
    }
    private static void loadMessages(String resourcePath) {
        try (InputStream input = I18nManager.class.getClassLoader().getResourceAsStream(resourcePath);
             InputStreamReader reader = new InputStreamReader(input);
             CSVReader csvReader = new CSVReader(reader)) {

            String[] headers = csvReader.readNext();
            if (headers == null) return;

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                String key = line[0];
                Map<String, String> translations = new HashMap<>();
                for (int i = 1; i < line.length; i++) {
                    translations.put(headers[i].trim(), line[i].trim());
                }
                MESSAGES.put(key, translations);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get(String key, String lang, Object... args) {
        String template = MESSAGES.getOrDefault(key, Collections.emptyMap())
                .getOrDefault(lang,
                        MESSAGES.getOrDefault(key, Collections.emptyMap())
                                .getOrDefault(DEFAULT_LANG, key)
                );

        return new MessageFormat(template, getLocaleForLanguage(lang)).format(args);
    }
    private static Locale getLocaleForLanguage(String lang) {
        return switch (lang) {
            case "es" -> Locale.forLanguageTag("es-AR");
            case "en" -> Locale.US;
            case "de" -> Locale.GERMANY;
            case "pt" -> Locale.forLanguageTag("pt-BR");
            case "fr" -> Locale.FRANCE;
            case "it" -> Locale.ITALY;
            default -> Locale.US;
        };
    }

}
