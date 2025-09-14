package org.controllers;


import static org.utils.SecurityUtils.checkAccountId;
import static org.utils.SecurityUtils.getCurrentAccountId;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.DTOs.registrations.RegistrationDTO;
import org.exceptions.RegistrationNotFoundException;
import org.exceptions.WrongUserException;
import org.model.enums.RegistrationState;
import org.model.events.Registration;
import org.services.AccountService;
import org.services.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.utils.PageNormalizer;

@RestController
@RequestMapping("/registrations")
public class RegistrationController {

  private final RegistrationService registrationService;
  private final AccountService accountService;

  public RegistrationController(RegistrationService registrationService, AccountService accountService) {
    this.registrationService = registrationService;
    this.accountService = accountService;
  }

  @GetMapping
  public ResponseEntity<List<RegistrationDTO>> getRegistrations(@RequestParam(name = "page", required = false) Integer page,
                                                                @RequestParam(name = "limit", required = false) Integer limit,
                                                                @RequestParam(name = "registrationState", required = false) RegistrationState registrationState) {
    UUID accountId = getCurrentAccountId();

    page = PageNormalizer.normalizeRegistrationsPageNumber(page);
    limit = PageNormalizer.normalizeRegistrationsPageLimit(limit);
    List<RegistrationDTO> registrations = accountService.getRegistrations(accountId, page, limit, registrationState);
    return ResponseEntity.ok(registrations);
  }

  @GetMapping("/{registrationId}")
  public ResponseEntity<?> getRegistrationByUserAndById(@PathVariable("registrationId") UUID registrationId) {
    UUID accountId = getCurrentAccountId();
    return registrationService.findByUserAndRegistrationId(accountId, registrationId)
        .<ResponseEntity<?>>map(ResponseEntity::ok)
        .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "La inscripción no existe o no pertenece al usuario")));
  }


  // Cancelar inscripción
  @PatchMapping("/{registrationId}")
  public ResponseEntity<?> cancelar(@PathVariable("registrationId") UUID registrationId, @RequestBody RegistrationDTO registrationDTO) {
    try {
      UUID accountId = getCurrentAccountId();
      Registration registration = registrationService.patchRegistration(registrationId, accountId, registrationDTO);
    }catch (WrongUserException e){
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse.create(e, HttpStatus.FORBIDDEN, e.getMessage()));
    }catch (RegistrationNotFoundException e){
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.create(e, HttpStatus.NOT_FOUND, e.getMessage()));
    }
    return ResponseEntity.noContent().build();
  }

}

