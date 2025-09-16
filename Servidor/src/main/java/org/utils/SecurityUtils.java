package org.utils;

import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

public class SecurityUtils {

  /**
   * Devuelve el accountId del usuario autenticado (extraído del token JWT).
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
   * Verifica si el accountId pasado corresponde al usuario autenticado.
   */
  public static boolean checkAccountId(UUID accountId) {
    UUID currentUserId = getCurrentAccountId();
    if (accountId == null || currentUserId == null) {
      return false;
    }
    return accountId.equals(currentUserId);
  }
}
