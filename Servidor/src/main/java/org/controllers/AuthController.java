package org.controllers;


import static org.DTOs.accounts.AccountResponseDTO.toAccountResponseDTO;
import static org.utils.SecurityUtils.getCurrentAccountId;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.DTOs.accounts.LoginRequestDTO;
import org.DTOs.accounts.RegisterRequestDTO;
import org.model.accounts.Account;
import org.model.accounts.OneTimeCode;
import org.DTOs.OneTimeCodeDTO;
import org.model.accounts.Role;
import org.services.AccountService;
import org.services.OneTimeCodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;
import org.utils.JwtUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AccountService accountService;
  private final OneTimeCodeService oneTimeCodeService;
  public AuthController(AccountService accountService, OneTimeCodeService oneTimeCodeService) {
    this.accountService = accountService;
      this.oneTimeCodeService = oneTimeCodeService;
  }

  // Registro de usuario normal
  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
    System.out.println("USUARIO REGISTRADO");
    try {
      Account account = accountService.register(request.getUsername(), request.getPassword(), false);
      return ResponseEntity.ok(toAccountResponseDTO(account));
    } catch (RuntimeException e) {
      return ResponseEntity
          .badRequest()
          .body(Map.of("error", e.getMessage()));
    }
  }

//  // Registro de admin
//  @PostMapping("/register-admin")
//  public ResponseEntity<?> registerAdmin(@RequestBody RegisterRequestDTO request) {
//    try {
//      Account account = accountService.register(request.getUsername(), request.getPassword(), true);
//      return ResponseEntity.ok(toAccountResponseDTO(account));
//    } catch (RuntimeException e) {
//      return ResponseEntity
//          .badRequest()
//          .body(Map.of("error", e.getMessage()));
//    }
//  }

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

  @PostMapping("/oneTimeCode")
  public ResponseEntity<?> createCode() {
    try {
      UUID accountId = getCurrentAccountId();
      Account account = accountService.getAccountById(accountId);

      Set<String> roles = account.getRoles()
              .stream()
              .map(Role::getName)
              .map(r -> r.replace("ROLE_", "")) //  le saco el prefijo
              .collect(Collectors.toSet());
        String token = JwtUtil.generateToken(account.getUsername(), account.getId(), roles);
      Map<String, Object> infoLogin = Map.of(
              "username", account.getUsername(),
              "id", account.getId(),
              "roles", roles,
              "token", token);

      OneTimeCode code = oneTimeCodeService.addNewCode(infoLogin);
      return ResponseEntity.ok(code);

    } catch (RuntimeException e) {
      return ResponseEntity
              .status(HttpStatus.UNAUTHORIZED)
              .body(Map.of("error", e.getMessage()));
    }
  }


  @GetMapping("/oneTimeCode")
  public ResponseEntity<?> getToken(@RequestBody OneTimeCodeDTO codeDTO) {
    try {
      OneTimeCode foundCode = oneTimeCodeService.findByUsername(codeDTO.getUsername());
      if (Objects.equals(foundCode.getCode(), codeDTO.getCode())){
        oneTimeCodeService.delete(foundCode);
        return ResponseEntity.ok(foundCode);
      }
      return ResponseEntity
              .status(HttpStatus.UNAUTHORIZED)
              .body(Map.of("description", "Wrong code"));
    } catch (RuntimeException e) {
      return ResponseEntity
              .status(HttpStatus.UNAUTHORIZED)
              .body(Map.of("error", e.getMessage()));
    }
  }
}
