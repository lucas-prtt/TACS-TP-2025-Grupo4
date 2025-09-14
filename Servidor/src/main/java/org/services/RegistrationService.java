package org.services;

import static org.DTOs.registrations.RegistrationDTO.toRegistrationDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import org.DTOs.registrations.RegistrationDTO;
import org.exceptions.*;
import org.model.events.Event;
import org.model.events.Registration;
import org.model.enums.RegistrationState;
import org.model.accounts.Account;
import org.repositories.AccountRepository;
import org.repositories.EventRepository;
import org.repositories.RegistrationRepository;
import org.springframework.stereotype.Service;


@Service
public class RegistrationService {

  private final RegistrationRepository registrationRepository;
  private final EventRepository eventRepository;
  private final AccountRepository accountRepository;
  ConcurrentHashMap<UUID, ReentrantLock> locksParticipants= new ConcurrentHashMap<>();
  public RegistrationService(RegistrationRepository registrationRepository, EventRepository eventRepository, AccountRepository accountRepository) {
    this.registrationRepository = registrationRepository;
    this.eventRepository = eventRepository;
    this.accountRepository = accountRepository;
  }

  // Crear una inscripción y devolver DTO
  public Optional<RegistrationDTO> register(UUID accountId, UUID eventId, RegistrationState state) {
    // Validar que no esté ya inscrito
    if (registrationRepository.existsByUserAndEvent(accountId, eventId)) {
      return Optional.empty(); // Ya existe inscripción
    }

    Optional<Event> event = eventRepository.findById(eventId);
    Optional<Account> account = accountRepository.findById(accountId.toString());

    if (event.isEmpty() || account.isEmpty()) {
      return Optional.empty();
    }

    Registration reg = new Registration(event.get(), account.get(), state);
    registrationRepository.save(reg);

    return Optional.of(toRegistrationDTO(reg));
  }

  // Buscar inscripción por id
  public Optional<RegistrationDTO> findById(UUID id) {
    return registrationRepository.findById(id).map(RegistrationDTO::toRegistrationDTO);
  }

  // Listar todas las inscripciones
  public List<RegistrationDTO> findAll() {
    return registrationRepository.findAll().stream()
        .map(RegistrationDTO::toRegistrationDTO)
        .collect(Collectors.toList());
  }

  // Listar inscripciones de un usuario
  public List<RegistrationDTO> findByAccountId(UUID accountId) {
    return registrationRepository.findByAccountId(accountId).stream()
        .map(RegistrationDTO::toRegistrationDTO)
        .collect(Collectors.toList());
  }

  // Listar inscripciones de un evento
  public List<RegistrationDTO> findByEventId(UUID eventId) {
    return registrationRepository.findByEventId(eventId).stream()
        .map(RegistrationDTO::toRegistrationDTO)
        .collect(Collectors.toList());
  }

  // Buscar inscripcion de un usuario dado el id de inscripcion
  public Optional<RegistrationDTO> findByUserAndRegistrationId(UUID accountId, UUID registrationId) {
    return registrationRepository.findById(registrationId)
        .filter(reg -> reg.getUser().getId().equals(accountId))
        .map(RegistrationDTO::toRegistrationDTO);
  }

  // Cancelar inscripción (usuario solo puede cancelar la suya)
  public Registration cancelRegistration(UUID registrationId, UUID accountId) {
    Optional<Registration> optReg = registrationRepository.findById(registrationId);

    if (optReg.isEmpty()) throw new RegistrationNotFoundException("No se encontro un registro con esa ID");

    Registration reg = optReg.get();

    // Validar que sea del usuario
    if (!reg.getUser().getId().equals(accountId)) throw new WrongUserException("El registro no pertenece al usuario dado");
    Event event = reg.getEvent();
    UUID eventId = event.getId();

    ReentrantLock lock = locksParticipants.computeIfAbsent(eventId, id -> new ReentrantLock());
    lock.lock();
    try {
      if(reg.getCurrentState() == RegistrationState.CONFIRMED){
        // Eliminar de participantes
        event.getParticipants().remove(reg);
        // Promocionar a alguien de la waitlist si corresponde
        event.promoteFromWaitlist();
      }else {
        event.getWaitList().remove(reg);
      }
      registrationRepository.cancelById(registrationId);

    }finally {
      lock.unlock();
      if (!lock.hasQueuedThreads()) {
        locksParticipants.remove(eventId, lock);
      }
    }


    return reg;

  }


  public Registration patchRegistration(UUID registrationId, UUID accountId, RegistrationDTO registrationDTO) {
    Optional<Registration> optReg = registrationRepository.findById(registrationId);
    if (optReg.isEmpty()){
      throw new RegistrationNotFoundException("No se encontro el registro");
    }
    Registration reg = optReg.get();
    if(registrationDTO.getState() != null && registrationDTO.getState() == RegistrationState.CANCELED){
      this.cancelRegistration(reg.getId(), accountId);
    }
    registrationRepository.save(reg);
    return reg;
  }


  public Registration registerParticipantToEvent(UUID eventId, UUID accountId) throws EventNotFoundException, UserNotFoundException, OrganizerRegisterException, AlreadyRegisteredException, EventRegistrationsClosedException{
    Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException("Evento no encontrado"));
    Account account = accountRepository.findById(String.valueOf(accountId))
            .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));


    ReentrantLock lock = locksParticipants.computeIfAbsent(eventId, id -> new ReentrantLock());
    lock.lock();
    // Verificar si el organizador intenta inscribirse a su propio evento
    if (event.getOrganizer().getId().equals(accountId)) {
      throw new OrganizerRegisterException("No se puede escribir a su propio evento");
    }

    //  Verificar si ya está inscripto
    if (event.getParticipants().stream().anyMatch(reg -> reg.getUser().getId().equals(accountId))) {
      throw new AlreadyParticipantException("Ya esta inscripto");
    }

    //  Verificar si ya está en waitlist
    if (event.getWaitList().stream().anyMatch(acc -> acc.getUser().getId().equals(accountId))) {
      throw new AlreadyInWaitlistException("Ya esta en la waitlist");
    }
    Registration registration = new Registration();
    registration.setEvent(event);
    registration.setUser(account);


    try {
      event.registerParticipant(registration);
      registrationRepository.save(registration);
    }
    finally {
        lock.unlock();
        if (!lock.hasQueuedThreads()) {
          locksParticipants.remove(eventId, lock);
        }
      }

    return registration;
  }
}
