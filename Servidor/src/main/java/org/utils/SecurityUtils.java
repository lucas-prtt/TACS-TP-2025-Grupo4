
package org.utils;

import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utilidad para operaciones relacionadas con la autenticación y el usuario actual en el contexto de seguridad.
 */
public class SecurityUtils {

  /**
   * Obtiene el accountId del usuario autenticado, extraído del principal en el contexto de seguridad.
   * @return UUID del usuario autenticado, o null si no hay usuario autenticado.
   */
  public static UUID getCurrentAccountId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth.getPrincipal() == null) {
      return null;
    }

    Object principal = auth.getPrincipal();

    if (principal instanceof UUID uuid) {
      return uuid;
    }

    return null;
  }

  /**
   * Verifica si el accountId recibido corresponde al usuario actualmente autenticado.
   * @param accountId UUID a comparar con el usuario autenticado.
   * @return true si el accountId corresponde al usuario autenticado, false en caso contrario.
   */
  public static boolean checkAccountId(UUID accountId) {
    UUID currentUserId = getCurrentAccountId();
    if (accountId == null || currentUserId == null) {
      return false;
    }
    return accountId.equals(currentUserId);
  }
}
