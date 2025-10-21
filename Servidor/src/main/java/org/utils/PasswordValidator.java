
package org.utils;

import java.util.ArrayList;
import java.util.List;
import org.exceptions.WeakPasswordException;

/**
 * Utilidad para validar contraseñas según criterios de seguridad definidos.
 */
public class PasswordValidator {

  /**
   * Valida la contraseña recibida según los siguientes criterios:
   * longitud mínima, mayúsculas, minúsculas, números, caracteres especiales y sin espacios.
   * Si la contraseña no cumple, lanza una WeakPasswordException con los errores encontrados.
   *
   * @param password Contraseña a validar.
   * @throws WeakPasswordException Si la contraseña no cumple los requisitos de seguridad.
   */
  public static void validatePassword(String password) {
    List<String> errors = new ArrayList<>();

    if (password.length() < 8 ) {
      errors.add("PROBLEM_8_CHARACTERS");
    }
    if (!password.matches(".*[A-Z].*")) {
      errors.add("PROBLEM_UPPER");
    }
    if (!password.matches(".*[a-z].*")) {
      errors.add("PROBLEM_LOWER");
    }
    if (!password.matches(".*\\d.*")) {
      errors.add("PROBLEM_NUMBER");
    }
    if (!password.matches(".*[!@#$%^&*()\\-_=+].*")) {
      errors.add("PROBLEM_SPECIAL_CHARACTER");
    }
    if (password.contains(" ")) {
      errors.add("PROBLEM_SPACE");
    }

    if (!errors.isEmpty()) {
      throw new WeakPasswordException(errors);
    }
  }
}

