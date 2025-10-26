package org.controllers;


import static org.utils.SecurityUtils.getCurrentAccountId;

import java.util.List;
import java.util.UUID;

import org.DTOs.registrations.RegistrationDTO;
import org.exceptions.RegistrationNotFoundException;
import org.model.enums.RegistrationState;
import org.model.events.Registration;
import org.services.AccountService;
import org.services.RegistrationService;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

  /**
   * Obtiene la lista de inscripciones del usuario autenticado, con paginación y filtrado por estado.
   * @param page Número de página (opcional)
   * @param limit Cantidad de elementos por página (opcional)
   * @param registrationState Estado de la inscripción (opcional)
   * @return ResponseEntity con la lista de inscripciones
   */
  @GetMapping
  public ResponseEntity<PagedModel<RegistrationDTO>> getRegistrations(@RequestParam(name = "page", required = false) Integer page,
                                                                      @RequestParam(name = "limit", required = false) Integer limit,
                                                                      @RequestParam(name = "registrationState", required = false) RegistrationState registrationState) {
    UUID accountId = getCurrentAccountId();

    page = PageNormalizer.normalizeRegistrationsPageNumber(page);
    limit = PageNormalizer.normalizeRegistrationsPageLimit(limit);
    Page<RegistrationDTO> registrations = registrationService.findByUser_IdAndRegistrationState(accountId, registrationState, page, limit);
    return ResponseEntity.ok(new PagedModel<>(registrations));
  }

  /**
   * Obtiene una inscripción específica por su ID, validando que pertenezca al usuario autenticado.
   * @param registrationId ID de la inscripción
   * @return ResponseEntity con la inscripción o error si no existe o no pertenece al usuario
   */
  @GetMapping("/{registrationId}")
  public ResponseEntity<?> getRegistrationByUserAndById(@PathVariable("registrationId") UUID registrationId) {
    UUID accountId = getCurrentAccountId();
    return ResponseEntity.ok(registrationService.findByUserAndRegistrationId(accountId, registrationId));
  }


  /**
   * Cancela una inscripción específica del usuario autenticado.
   * @param registrationId ID de la inscripción a cancelar
   * @param registrationDTO DTO con los datos de la inscripción
   * @return ResponseEntity sin contenido si se cancela correctamente, o error si no corresponde al usuario o no existe
   */
  @PatchMapping("/{registrationId}")
  public ResponseEntity<?> cancelar(@PathVariable("registrationId") UUID registrationId, @RequestBody RegistrationDTO registrationDTO) {
    UUID accountId = getCurrentAccountId();
    Registration registration = registrationService.patchRegistration(registrationId, accountId, registrationDTO);
    return ResponseEntity.noContent().build();
  }
}

