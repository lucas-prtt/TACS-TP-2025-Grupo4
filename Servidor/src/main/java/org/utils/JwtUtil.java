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

public class JwtUtil {

  private static final String SECRET_KEY = "EstaEsUnaClaveSecretaMuyMuyLargaParaJWT1234567890!!!"; // DEBE IR EN UNA VARIABLE DE ENTORNO
  private static final Key KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
  private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hora

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


  public static Claims validateToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(KEY)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
