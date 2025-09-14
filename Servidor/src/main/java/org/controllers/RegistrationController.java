package org.controllers;


import static org.utils.SecurityUtils.checkAccountId;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.exceptions.RegistrationNotFoundException;
import org.exceptions.WrongUserException;
import org.model.events.Registration;
import org.services.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("accounts/{accountId}/registrations")
public class RegistrationController {

  private final RegistrationService registrationService;

  public RegistrationController(RegistrationService registrationService) {
    this.registrationService = registrationService;
  }

  @GetMapping("/{registrationId}")
  public ResponseEntity<?> getRegistrationByUserAndById(@PathVariable("accountId") UUID accountId,
                                                        @PathVariable("registrationId") UUID registrationId) {
    if(!checkAccountId(accountId)){
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    return registrationService.findByUserAndRegistrationId(accountId, registrationId)
        .<ResponseEntity<?>>map(ResponseEntity::ok)
        .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "La inscripción no existe o no pertenece al usuario")));
  }


  // Cancelar inscripción
  @DeleteMapping("/{registrationId}")
  public ResponseEntity<?> cancel(@PathVariable("accountId") UUID accountId, @PathVariable("registrationId") UUID registrationId) {
    try {
      if(!checkAccountId(accountId)){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      }
      Registration registration = registrationService.cancelRegistration(registrationId, accountId);
    }catch (WrongUserException e){
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse.create(e, HttpStatus.FORBIDDEN, e.getMessage()));
    }catch (RegistrationNotFoundException e){
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.create(e, HttpStatus.NOT_FOUND, e.getMessage()));
    }
    return ResponseEntity.noContent().build();
  }


}

