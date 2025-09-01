package org.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.DTOs.RegistrationDTO;
import org.dominio.events.Event;
import org.dominio.events.Registration;
import org.dominio.events.RegistrationState;
import org.dominio.usuarios.Account;
import org.repositories.AccountRepository;
import org.repositories.EventRepository;
import org.repositories.RegistrationRepository;
import org.springframework.stereotype.Service;


@Service
public class RegistrationService {

  private final RegistrationRepository registrationRepository;
  private final EventRepository eventRepository;
  private final AccountRepository accountRepository;

  public RegistrationService(RegistrationRepository registrationRepository, EventRepository eventRepository, AccountRepository accountRepository) {
    this.registrationRepository = registrationRepository;
    this.eventRepository = eventRepository;
    this.accountRepository = accountRepository;
  }

  // Crear una inscripción y devolver DTO
  public Optional<RegistrationDTO> register(UUID userId, UUID eventId, RegistrationState state) {
    // Validar que no esté ya inscrito
    if (registrationRepository.existsByUserAndEvent(userId, eventId)) {
      return Optional.empty(); // Ya existe inscripción
    }

    Optional<Event> event = eventRepository.findById(eventId);
    Optional<Account> account = accountRepository.findById(userId.toString());

    if (event.isEmpty() || account.isEmpty()) {
      return Optional.empty();
    }

    Registration reg = new Registration(event.get(), account.get(), state);
    registrationRepository.save(reg);

    return Optional.of(toDto(reg));
  }

  // Buscar inscripción por id
  public Optional<RegistrationDTO> findById(UUID id) {
    return registrationRepository.findById(id).map(this::toDto);
  }

  // Listar todas las inscripciones
  public List<RegistrationDTO> findAll() {
    return registrationRepository.findAll().stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  // Listar inscripciones de un usuario
  public List<RegistrationDTO> findByUserId(UUID userId) {
    return registrationRepository.findByUserId(userId).stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  // Listar inscripciones de un evento
  public List<RegistrationDTO> findByEventId(UUID eventId) {
    return registrationRepository.findByEventId(eventId).stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  // Buscar inscripcion de un usuario dado el id de inscripcion
  public Optional<RegistrationDTO> findByUserAndRegistrationId(UUID userId, UUID registrationId) {
    return registrationRepository.findById(registrationId)
        .filter(reg -> reg.getUser().getId().equals(userId))
        .map(this::toDto);
  }

  // Cancelar inscripción (usuario solo puede cancelar la suya)
  public boolean cancelRegistration(UUID regId, UUID userId) {
    Optional<Registration> regOpt = registrationRepository.findById(regId);

    if (regOpt.isEmpty()) return false;

    Registration reg = regOpt.get();
    if (!reg.getUser().getId().equals(userId)) {
      return false; // No tiene permiso
    }

    return registrationRepository.deleteById(regId);
  }

  private RegistrationDTO toDto(Registration reg) {
    return new RegistrationDTO(
        reg.getId(),
        reg.getUser().getId(),
        reg.getEvent().getId(),
        reg.getState(),
        reg.getDateTime()
    );
  }

}
