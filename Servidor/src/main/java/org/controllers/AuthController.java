package org.controllers;


import static org.DTOs.accounts.AccountResponseDTO.toAccountResponseDTO;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.DTOs.accounts.AccountResponseDTO;
import org.DTOs.accounts.LoginRequestDTO;
import org.DTOs.accounts.RegisterRequestDTO;
import org.model.accounts.Account;
import org.model.accounts.Role;
import org.services.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.utils.JwtUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AccountService accountService;

  public AuthController(AccountService accountService) {
    this.accountService = accountService;
  }

  // Registro de usuario normal
  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
    try {
      Account account = accountService.register(request.getUsername(), request.getPassword(), false);
      return ResponseEntity.ok(toAccountResponseDTO(account));
    } catch (RuntimeException e) {
      return ResponseEntity
          .badRequest()
          .body(Map.of("error", e.getMessage()));
    }
  }

  // Registro de admin
  @PostMapping("/register-admin")
  public ResponseEntity<?> registerAdmin(@RequestBody RegisterRequestDTO request) {
    try {
      Account account = accountService.register(request.getUsername(), request.getPassword(), true);
      return ResponseEntity.ok(toAccountResponseDTO(account));
    } catch (RuntimeException e) {
      return ResponseEntity
          .badRequest()
          .body(Map.of("error", e.getMessage()));
    }
  }

  // Login
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
    try {
      Account account = accountService.login(request.getUsername(), request.getPassword());

      Set<String> roles = account.getRoles()
          .stream()
          .map(Role::getName)
          .map(r -> r.replace("ROLE_", "")) //  le saco el prefijo
          .collect(Collectors.toSet());

      String token = JwtUtil.generateToken(account.getUsername(), account.getId(), roles);


      return ResponseEntity.ok(Map.of(
          "username", account.getUsername(),
          "id", account.getId(),
          "roles", roles,
          "token", token
      ));
    } catch (RuntimeException e) {
      return ResponseEntity
          .status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("error", e.getMessage()));
    }
  }

}
