package org.utils;

import java.util.Random;

public class RandomWordGenerator {
    private static final String[] WORDS = {
            "manzana", "banana", "cereza", "durazno", "arándano",
            "higo", "uva", "melón", "kiwi", "limón",
            "mango", "azúcar", "naranja", "papaya", "membrillo",
            "empanada de pollo", "ají", "empanada de carne", "tira de asado", "chocolate"
    };
    private static final Random RANDOM = new Random();
    public static String randomWord() {
        int index = RANDOM.nextInt(WORDS.length);
        return WORDS[index];
    }
}
