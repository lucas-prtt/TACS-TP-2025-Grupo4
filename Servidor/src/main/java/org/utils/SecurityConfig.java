
package org.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad para la aplicación. Define filtros, reglas de autorización y beans relacionados con autenticación y encriptación de contraseñas.
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

  /** Filtro de autenticación JWT inyectado. */
  private final JwtAuthenticationFilter jwtAuthFilter;

  /**
   * Configura la cadena de filtros de seguridad y las reglas de autorización para los endpoints.
   *
   * @param http Objeto HttpSecurity para configurar la seguridad.
   * @return SecurityFilterChain configurada.
   * @throws Exception Si ocurre un error en la configuración.
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers( "/auth/register", "/auth/login").permitAll()
            .requestMatchers(HttpMethod.GET, "/auth/oneTimeCode").permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated())
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  /**
   * Bean para encriptar contraseñas usando BCrypt.
   * @return PasswordEncoder basado en BCrypt.
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Bean para obtener el AuthenticationManager de la configuración de autenticación.
   * @param authConfig Configuración de autenticación.
   * @return AuthenticationManager configurado.
   * @throws Exception Si ocurre un error al obtener el AuthenticationManager.
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }
}

