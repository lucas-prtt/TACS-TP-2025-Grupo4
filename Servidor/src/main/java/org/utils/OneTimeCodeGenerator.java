package org.utils;

import java.security.SecureRandom;
import java.util.Random;

public class OneTimeCodeGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LENGTH = 6; // 36 ^ 6 = 2.176.782.336
    private static final Random RANDOM = new Random();
    public static char generateChar(){
        return CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length()));
    }

    /**
     * Genera aleatoriamente un codigo de 6 caracteres
     * El codigo esta compuesto de letras mayusculas y digitos
     * @return El codigo generado
     */
    public static String generateCode(){
        StringBuilder randomCodeBuilder = new StringBuilder();
        for (int i = 0; i<LENGTH; i++){
            randomCodeBuilder.append(generateChar());
        }
        return randomCodeBuilder.toString();
    }
}
