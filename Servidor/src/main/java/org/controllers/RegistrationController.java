package org.controllers;


import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.DTOs.EventDTO;
import org.DTOs.RegistrationDTO;
import org.dominio.events.Registration;
import org.dominio.usuarios.Account;
import org.exceptions.EventNotFoundException;
import org.services.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users/{accountId}/registrations")
public class RegistrationController {

  private final RegistrationService registrationService;

  public RegistrationController(RegistrationService registrationService) {
    this.registrationService = registrationService;
  }

//  // Obtener todas las inscripciones de un usuario
//  @GetMapping
//  public ResponseEntity<List<RegistrationDTO>> getAllByUser(@PathVariable("accountId")  UUID accountId) {
//    return ResponseEntity.ok(registrationService.findByAccountId(accountId));
//  }

  @GetMapping("/{registrationId}")
  public ResponseEntity<?> getRegistrationByUserAndById(@PathVariable("accountId") UUID accountId,
                                                        @PathVariable("registrationId") UUID registrationId) {
    return registrationService.findByUserAndRegistrationId(accountId, registrationId)
        .<ResponseEntity<?>>map(ResponseEntity::ok)
        .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "La inscripción no existe o no pertenece al usuario")));
  }


  // Cancelar inscripción
  @DeleteMapping("/{registrationId}")
  public ResponseEntity<?> cancel(@PathVariable("accountId") UUID accountId, @PathVariable("registrationId") UUID registrationId) {
    boolean ok = registrationService.cancelRegistration(registrationId, accountId);
    if (!ok) {
      return ResponseEntity.status(403)
          .body(Map.of("error", "No tienes permiso o la inscripción no existe"));
    }

    return ResponseEntity.ok(Map.of("canceled", true));
  }


}

