
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
      errors.add("La contraseña debe tener entre 8 caracteres");
    }
    if (!password.matches(".*[A-Z].*")) {
      errors.add("Debe contener al menos una letra mayúscula");
    }
    if (!password.matches(".*[a-z].*")) {
      errors.add("Debe contener al menos una letra minúscula");
    }
    if (!password.matches(".*\\d.*")) {
      errors.add("Debe contener al menos un número");
    }
    if (!password.matches(".*[!@#$%^&*()\\-_=+].*")) {
      errors.add("Debe contener al menos un carácter especial (!@#$%^&*()-_+=)");
    }
    if (password.contains(" ")) {
      errors.add("No debe contener espacios");
    }

    if (!errors.isEmpty()) {
      throw new WeakPasswordException(String.join("; ", errors));
    }
  }
}

