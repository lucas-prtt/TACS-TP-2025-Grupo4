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

    /**
     * Crea una inscripción y devuelve su DTO. Si ya existe, retorna vacío.
     * @param accountId ID del usuario
     * @param eventId ID del evento
     * @param state Estado inicial de la inscripción
     * @return Optional con el DTO de la inscripción creada, vacío si ya existe
     */
    public Optional<RegistrationDTO> register(UUID accountId, UUID eventId, RegistrationState state) {
        // Validar que no esté ya inscrito
        if (registrationRepository.existsByUser_IdAndEvent_Id(accountId, eventId)) {
          return Optional.empty(); // Ya existe inscripción
        }

        Optional<Event> event = eventRepository.findById(eventId);
        Optional<Account> account = accountRepository.findById(UUID.fromString(accountId.toString()));

        if (event.isEmpty() || account.isEmpty()) {
          return Optional.empty();
        }

        Registration reg = new Registration(event.get(), account.get(), state);
        registrationRepository.save(reg);

        return Optional.of(toRegistrationDTO(reg));
    }

    /**
     * Busca una inscripción por su ID y devuelve su DTO.
     * @param id ID de la inscripción
     * @return Optional con el DTO de la inscripción
     */
    public Optional<RegistrationDTO> findById(UUID id) {
        return registrationRepository.findById(id).map(RegistrationDTO::toRegistrationDTO);
    }

    /**
     * Devuelve la lista de todas las inscripciones en formato DTO.
     * @return Lista de DTOs de inscripciones
     */
    public List<RegistrationDTO> findAll() {
        return registrationRepository.findAll().stream()
            .map(RegistrationDTO::toRegistrationDTO)
            .collect(Collectors.toList());
    }

    /**
     * Devuelve la lista de inscripciones de un usuario por ID en formato DTO.
     * @param accountId ID del usuario
     * @return Lista de DTOs de inscripciones
     */
    public List<RegistrationDTO> findByAccountId(UUID accountId) {
        return registrationRepository.findByUser_Id(accountId).stream()
            .map(RegistrationDTO::toRegistrationDTO)
            .collect(Collectors.toList());
    }

    /**
     * Devuelve la lista de inscripciones de un evento en formato DTO.
     * @param eventId ID del evento
     * @return Lista de DTOs de inscripciones
     */
    public List<RegistrationDTO> findByEventId(UUID eventId) {
        return registrationRepository.findByEvent_Id(eventId).stream()
            .map(RegistrationDTO::toRegistrationDTO)
            .collect(Collectors.toList());
    }

    /**
     * Busca una inscripción por usuario y por ID de inscripción.
     * @param accountId ID del usuario
     * @param registrationId ID de la inscripción
     * @return Optional con el DTO de la inscripción
     */
    public Optional<RegistrationDTO> findByUserAndRegistrationId(UUID accountId, UUID registrationId) {
        return registrationRepository.findById(registrationId)
            .filter(reg -> reg.getUser().getId().equals(accountId))
            .map(RegistrationDTO::toRegistrationDTO);
    }

    /**
     * Cancela una inscripción, solo si pertenece al usuario.
     * @param registrationId ID de la inscripción
     * @param accountId ID del usuario
     * @return La inscripción cancelada
     * @throws RegistrationNotFoundException si no existe la inscripción
     * @throws WrongUserException si la inscripción no pertenece al usuario
     */
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
            reg.setState(RegistrationState.CANCELED);
            registrationRepository.save(reg);
            eventRepository.save(event);
        }finally {
            lock.unlock();
            if (!lock.hasQueuedThreads()) {
                locksParticipants.remove(eventId, lock);
            }
        }
        return reg;
    }

    /**
     * Actualiza parcialmente una inscripción, permitiendo cancelarla.
     * @param registrationId ID de la inscripción
     * @param accountId ID del usuario
     * @param registrationDTO DTO con los datos a actualizar
     * @return La inscripción actualizada
     * @throws RegistrationNotFoundException si no existe la inscripción
     */
    public Registration patchRegistration(UUID registrationId, UUID accountId, RegistrationDTO registrationDTO) {
        if(registrationDTO.getState() != null && registrationDTO.getState() == RegistrationState.CANCELED){
            this.cancelRegistration(registrationId, accountId); // Ya hace save
        }
        Optional<Registration> optReg = registrationRepository.findById(registrationId);
        if (optReg.isEmpty()){
            throw new RegistrationNotFoundException("No se encontro el registro"); // Ya deberia ser verificado por cancelRegistration, pero lo pongo igual por las dudas
        }
        return optReg.get();
    }

    /**
     * Inscribe un usuario a un evento, validando reglas de negocio.
     * @param eventId ID del evento
     * @param accountId ID del usuario
     * @return La inscripción realizada
     * @throws EventNotFoundException si el evento no existe
     * @throws UserNotFoundException si el usuario no existe
     * @throws OrganizerRegisterException si el organizador intenta inscribirse
     * @throws AlreadyRegisteredException si el usuario ya está inscripto
     * @throws EventRegistrationsClosedException si el evento está cerrado
     */
    public Registration registerParticipantToEvent(UUID eventId, UUID accountId) throws EventNotFoundException, UserNotFoundException, OrganizerRegisterException, AlreadyRegisteredException, EventRegistrationsClosedException{
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Evento no encontrado"));
        Account account = accountRepository.findById(UUID.fromString(String.valueOf(accountId)))
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));


        ReentrantLock lock = locksParticipants.computeIfAbsent(eventId, id -> new ReentrantLock());
        Registration registration;
        lock.lock();
        try {
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
        registration = new Registration();
        registration.setEvent(event);
        registration.setUser(account);

            event.registerParticipant(registration);
            registrationRepository.save(registration);
            eventRepository.save(event);
            accountRepository.save(account);        // Se le agrega la registration en event.registerParticipant(registration)
        }
        finally {
            lock.unlock();
            if (!lock.hasQueuedThreads()) {
                locksParticipants.remove(eventId, lock);
            }
        }

        return registration;
    }

    /**
     * Devuelve todas las inscripciones que alguna vez estuvieron en WAITLIST.
     * @return Lista de inscripciones en WAITLIST
     */
    public List<Registration> findAllThatWereInWaitlist() {
        return registrationRepository.findAll().stream()
                .filter(reg -> reg.getHistory() != null && reg.getHistory().stream()
                        .anyMatch(change -> change.getToState() == RegistrationState.WAITLIST))
                .collect(Collectors.toList());
    }

    /**
     * Devuelve todas las inscripciones que fueron promovidas de WAITLIST a CONFIRMED.
     * @return Lista de inscripciones promovidas
     */
    public List<Registration> findAllPromotedFromWaitlist() {
        return registrationRepository.findAll().stream()
                .filter(reg -> reg.getHistory() != null && reg.getHistory().stream()
                        .anyMatch(change -> change.getFromState() == RegistrationState.WAITLIST
                                && change.getToState() == RegistrationState.CONFIRMED))
                .collect(Collectors.toList());
    }
}
