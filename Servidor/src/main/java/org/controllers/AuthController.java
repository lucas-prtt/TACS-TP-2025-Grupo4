package org.controllers;


import static org.DTOs.accounts.AccountResponseDTO.toAccountResponseDTO;
import static org.utils.SecurityUtils.getCurrentAccountId;

import java.util.*;
import java.util.stream.Collectors;

import org.DTOs.accounts.LoginRequestDTO;
import org.DTOs.accounts.RegisterRequestDTO;
import org.exceptions.AccountNotFoundException;
import org.exceptions.WrongOneTimeCodeException;
import org.model.accounts.Account;
import org.model.accounts.OneTimeCode;
import org.model.accounts.Role;
import org.services.AccountService;
import org.services.OneTimeCodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utils.JwtUtil;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
public class AuthController {

  private final AccountService accountService;
  private final OneTimeCodeService oneTimeCodeService;
  public AuthController(AccountService accountService, OneTimeCodeService oneTimeCodeService) {
    this.accountService = accountService;
      this.oneTimeCodeService = oneTimeCodeService;
  }

  /**
   * Registra un usuario normal en el sistema.
   * @param request DTO con los datos de registro (usuario y contraseña)
   * @return ResponseEntity con el usuario registrado o error
   */
  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request, @RequestHeader(name = "Accept-Language", required = false) String lang) {
    System.out.println("USUARIO REGISTRADO");
    Account account = accountService.register(request.getUsername(), request.getPassword(), false);
    return ResponseEntity.ok(toAccountResponseDTO(account));
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

  /**
   * Realiza el login de un usuario.
   * @param request DTO con los datos de login (usuario y contraseña)
   * @return ResponseEntity con los datos del usuario y el token JWT, o error
   */
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequestDTO request, @RequestHeader(name = "Accept-Language", required = false) String lang) {
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
  }
    @GetMapping("/checkUser")
    public ResponseEntity<?> checkUser(@RequestParam(value = "username", required = true) String username, @RequestHeader(name = "Accept-Language", required = false) String lang) {
        try{
            Account account = accountService.getAccountByUsername(username);
            return ResponseEntity.ok(account.getId());
        }catch (AccountNotFoundException ex){
            return ResponseEntity.notFound().build();
        }
    }

  /**
   * Genera un código de un solo uso (OneTimeCode) para el usuario autenticado.
   * @return ResponseEntity con el código generado o error
   */
  @PostMapping("/oneTimeCode")
  public ResponseEntity<?> createCode(@RequestHeader(name = "Accept-Language", required = false) String lang) {
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
  }


  /**
   * Verifica el código de un solo uso y retorna el token de login si es correcto.
   * @param username Nombre de usuario
   * @param code Código de un solo uso
   * @return ResponseEntity con el token de login o error
   */
  @GetMapping("/oneTimeCode")
  public ResponseEntity<?> getToken(@RequestParam(name = "username", required = true) String username,
                                    @RequestParam(name = "code", required = true) String code,
                                    @RequestHeader(name = "Accept-Language", required = false) String lang) {
      List<OneTimeCode> foundCodes = oneTimeCodeService.findByUsername(username);
      for(OneTimeCode foundCode : foundCodes){
        if (Objects.equals(foundCode.getCode(), code) && foundCode.isValid()){
          oneTimeCodeService.delete(foundCode);
          return ResponseEntity.ok(foundCode.getCosaDelLogueo());
        }
      }
      // Si la concurrencia hace que se invalide justo o se puso mal el código
      throw new WrongOneTimeCodeException();
  }
}
