







package org.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

/**
 * Utilidad para la generación y validación de tokens JWT en el sistema.
 */
public class JwtUtil {

  /** Clave secreta utilizada para firmar los tokens. */
  private static final String SECRET_KEY = System.getenv("EVENTOS_SERVER_SECRET_KEY");
  /** Llave criptográfica generada a partir de la clave secreta. */
  private static final Key KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
  /** Tiempo de expiración del token en milisegundos. */
  private static final long EXPIRATION_TIME = 1000L * 60 * ConfigManager.getInstance().getIntegerOrElse("security.token.expiration.minutes", 60); // 1 hora

  /**
   * Genera un token JWT para el usuario especificado.
   * @param username Nombre de usuario.
   * @param accountId Identificador único de la cuenta.
   * @param roles Conjunto de roles asociados al usuario.
   * @return Token JWT generado como String.
   */
  public static String generateToken(String username, UUID accountId, Set<String> roles) {
    return Jwts.builder()
        .setSubject(username)
        .claim("accountId", accountId.toString())
        .claim("roles", roles)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(KEY, SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * Valida un token JWT y devuelve los claims contenidos en él.
   * @param token Token JWT a validar.
   * @return Claims extraídos del token si es válido.
   * @throws io.jsonwebtoken.JwtException si el token es inválido o está expirado.
   */
  public static Claims validateToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(KEY)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
