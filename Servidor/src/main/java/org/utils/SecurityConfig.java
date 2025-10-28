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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

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
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            // Endpoints públicos de autenticación
            .requestMatchers("/auth/register", "/auth/login", "/auth/validate", "/auth/checkUser").permitAll()
            .requestMatchers(HttpMethod.GET, "/auth/oneTimeCode").permitAll()
            
            // Endpoints de eventos - permitir a usuarios autenticados
            .requestMatchers(HttpMethod.GET, "/events", "/events/**").permitAll() // Permitir lectura pública
            .requestMatchers(HttpMethod.POST, "/events").authenticated() // Crear evento requiere autenticación
            .requestMatchers(HttpMethod.POST, "/events/*/registrations").authenticated() // Registrarse requiere autenticación
            .requestMatchers(HttpMethod.PATCH, "/events/**").authenticated() // Actualizar requiere autenticación
            .requestMatchers(HttpMethod.GET, "/events/organized-events").authenticated() // Requiere autenticación
            .requestMatchers(HttpMethod.GET, "/events/*/registrations").authenticated() // Ver participantes requiere autenticación
            
            // Endpoints de admin
            .requestMatchers("/admin/**").hasRole("ADMIN")
            
            // El resto requiere autenticación
            .anyRequest().authenticated())
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  /**
   * Configura CORS para permitir peticiones desde el frontend.
   * @return CorsConfigurationSource configurada.
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:3000"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
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

