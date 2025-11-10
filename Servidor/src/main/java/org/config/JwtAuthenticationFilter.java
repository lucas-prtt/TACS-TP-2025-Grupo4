
package org.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.utils.JwtUtil;

/**
 * Filtro de autenticación JWT para validar el token en cada request y establecer el usuario autenticado en el contexto de seguridad.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  /**
   * Procesa cada request HTTP verificando el header Authorization y validando el token JWT.
   * Si el token es válido, establece el usuario autenticado en el contexto de Spring Security.
   * Si el token es inválido, responde con estado UNAUTHORIZED.
   *
   * @param request Request HTTP entrante.
   * @param response Response HTTP saliente.
   * @param filterChain Cadena de filtros de la request.
   * @throws ServletException Si ocurre un error en el filtro.
   * @throws IOException Si ocurre un error de IO.
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

    String header = request.getHeader("Authorization");

    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);
      try {
        var claims = JwtUtil.validateToken(token);
        UUID accountId = UUID.fromString(claims.get("accountId", String.class));
        @SuppressWarnings("unchecked")
        var roles = ((List<String>) claims.get("roles")).stream()
            .map(r -> "ROLE_" + r)  // Spring Security requiere prefijo ROLE_
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

        var auth = new UsernamePasswordAuthenticationToken(accountId, null, roles);
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(auth);

      } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }
    }

    filterChain.doFilter(request, response);
  }
}