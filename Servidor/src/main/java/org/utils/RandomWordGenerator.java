
package org.utils;

import java.util.Random;

/**
 * Utilidad para obtener palabras aleatorias de una lista predefinida.
 */
public class RandomWordGenerator {
    /** Lista de palabras disponibles para selección aleatoria. */
    private static final String[] WORDS = {
            "manzana", "banana", "cereza", "durazno", "arándano",
            "higo", "uva", "melón", "kiwi", "limón",
            "mango", "azúcar", "naranja", "papaya", "membrillo",
            "empanada", "ají", "empanada", "asado", "chocolate"
    };
    /** Generador de números aleatorios. */
    private static final Random RANDOM = new Random();

    /**
     * Devuelve una palabra aleatoria de la lista predefinida.
     * @return Palabra seleccionada aleatoriamente.
     */
    public static String randomWord() {
        int index = RANDOM.nextInt(WORDS.length);
        return WORDS[index];
    }
}
