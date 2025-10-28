package org.services;

import static org.DTOs.registrations.RegistrationDTO.toRegistrationDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.DTOs.registrations.RegistrationDTO;
import org.exceptions.*;
import org.model.enums.EventState;
import org.model.events.Event;
import org.model.events.Registration;
import org.model.enums.RegistrationState;
import org.model.accounts.Account;
import org.repositories.AccountRepository;
import org.repositories.EventRepository;
import org.repositories.RegistrationRepository;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import com.mongodb.client.result.UpdateResult;
import org.springframework.dao.DuplicateKeyException;


@Service
public class RegistrationService {
    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final AccountRepository accountRepository;
    private final MongoTemplate mongoTemplate;

    public RegistrationService(RegistrationRepository registrationRepository, EventRepository eventRepository, AccountRepository accountRepository,MongoTemplate mongoTemplate) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.accountRepository = accountRepository;
        this.mongoTemplate = mongoTemplate;
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
    public RegistrationDTO findByUserAndRegistrationId(UUID accountId, UUID registrationId) {
        Registration reg = registrationRepository.findById(registrationId).orElseThrow(RegistrationNotFoundException::new);
        if(reg.getUser().getId().equals(accountId)){
            return toRegistrationDTO(reg);
        }
        throw new RegistrationOfDifferentUserException();
    }

    /**
     * Cancela una inscripción, solo si pertenece al usuario.
     * @param registrationId UUID de inscripción a cancelar
     * @return La inscripción cancelada
     * @throws AlreadyCanceledException si ya está cancelada
     */
    @Retryable(retryFor = { TransientDataAccessException.class })
    @Transactional // ✅ 1. Garantía de Consistencia
    public Registration cancelRegistration(UUID registrationId) {
        // 1. RELEER DENTRO DE LA TRANSACCIÓN
        Registration registration = registrationRepository.findById(registrationId)
            .orElseThrow(RegistrationNotFoundException::new);
        if (registration.getCurrentState() == RegistrationState.CANCELED) {
            throw new AlreadyCanceledException("El registro ya esta cancelado");
        }

        UUID eventId = registration.getEvent().getId();
        RegistrationState oldState = registration.getCurrentState();

        // --- 2. MARCAR INSCRIPCIÓN COMO CANCELADA ---
        // Esta es la primera escritura en la transacción.
        registration.setState(RegistrationState.CANCELED);
        registrationRepository.save(registration);

        Query eventQuery = new Query(Criteria.where("_id").is(eventId));

        if (oldState == RegistrationState.CONFIRMED) {
            // --- 3. ESTABA CONFIRMADO (AFECTA CUPOS) ---
            // 3a. Intentamos 'pop' atómico de la waitlist.
            // Esta es la operación que bloquea a otras cancelaciones concurrentes.
            Update popFromWaitlist = new Update().pop("waitList", Update.Position.FIRST); // Saca el primero

            Event eventBeforePop = mongoTemplate.findAndModify(
                eventQuery,
                popFromWaitlist,
                new FindAndModifyOptions().returnNew(false), // Devuelve el doc ANTIGUO
                Event.class
            );


            if (eventBeforePop != null && !eventBeforePop.getWaitList().isEmpty()) {
                // --- 3b. ÉXITO: Había alguien en la waitlist ---
                Registration promotedReg = eventBeforePop.getWaitList().getFirst();

                // Actualizamos la inscripción del promovido
                promotedReg.setState(RegistrationState.CONFIRMED);
                registrationRepository.save(promotedReg);

                // ✅ --- CORRECCIÓN: Separar $pull y $push ---
                // Paso 1: Atómicamente quitar al que canceló
                Update pullCancelling = new Update().pull("participants", registration);
                mongoTemplate.updateFirst(eventQuery, pullCancelling, Event.class);

                // Paso 2: Atómicamente añadir al promovido
                Update pushPromoted = new Update().push("participants", promotedReg);
                mongoTemplate.updateFirst(eventQuery, pushPromoted, Event.class);

            } else {
                // --- 3c. FRACASO: La waitlist estaba vacía ---
                // Liberamos un cupo Y quitamos al participante atómicamente.
                Update freeUpSeat = new Update()
                    .inc("availableSeats", 1)
                    .pull("participants", registration);
                mongoTemplate.updateFirst(eventQuery, freeUpSeat, Event.class);
            }

        } else {
            // --- 4. ESTABA EN WAITLIST  ---
            // Solo lo quitamos de la lista, no afecta cupos.
            System.out.println("DEBUG: Cancelando WAITLIST - Thread: " + Thread.currentThread().getName());
            Update pullFromWaitlist = new Update().pull("waitList", registration);
            mongoTemplate.updateFirst(eventQuery, pullFromWaitlist, Event.class);
        }

        return registration;
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
        // Verifica que exista registration con ese ID
        Optional<Registration> optReg = registrationRepository.findById(registrationId);
        if (optReg.isEmpty()){
            throw new RegistrationNotFoundException(); // Ya deberia ser verificado por cancelRegistration, pero lo pongo igual por las dudas
        }
        Registration reg = optReg.get();
        // Verifica que sea una registration del usuario
        if (!reg.getUser().getId().equals(accountId))
            throw new RegistrationOfDifferentUserException();

        if(registrationDTO.getState() != null
            && registrationDTO.getState() == RegistrationState.CANCELED
            && reg.getCurrentState() != RegistrationState.CANCELED)
        {
                cancelRegistration(registrationId); // Ya hace save
        }

        return reg;
    }

    /**
     * Inscribe un usuario a un evento, validando reglas de negocio.
     * @param eventId ID del evento
     * @param accountId ID del usuario
     * @return La inscripción realizada
     * @throws EventNotFoundException si el evento no existe
     * @throws AccountNotFoundException si el usuario no existe
     * @throws OrganizerRegisterException si el organizador intenta inscribirse
     * @throws AlreadyRegisteredException si el usuario ya está inscripto
     * @throws EventRegistrationsClosedException si el evento está cerrado
     */

    /**
     * @Retryable: Si la transacción falla con un error transitorio
     * (un choque de concurrencia), vuelve a intentarlo
     * automáticamente (hasta 3 veces por defecto).
     */
    @Retryable(retryFor = { TransientDataAccessException.class })
    @Transactional // Garantía de Consistencia
    public Registration registerParticipantToEvent(UUID eventId, UUID accountId)
        throws EventNotFoundException, OrganizerRegisterException, AlreadyRegisteredException,
        AlreadyInWaitlistException, EventRegistrationsClosedException, AccountNotFoundException {

        // --- 1. LECTURAS Y VALIDACIONES PREVIAS ---
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException("Evento no encontrado"));
        Account account = accountRepository.findById(accountId)
            .orElseThrow(AccountNotFoundException::new);

        // Validaciones de lógica de negocio (no tocan la BD)

        // VALIDACIÓN DE TIEMPO (Debe ir al inicio)
        if (!event.isRegistrationsOpen()) {
            throw new EventRegistrationsClosedException("El evento se encuentra en estado:" + event.getEventState().toString());
        }

        if (event.getOrganizer().getId().equals(accountId)) {
            throw new OrganizerRegisterException("No se puede escribir a su propio evento");
        }

        // Chequeos optimistas (tus 'if').
        // Son vulnerables a race conditions, pero la red de seguridad (paso 2) nos protege.
        if (event.getParticipants().stream().anyMatch(reg -> reg.getUser().getId().equals(accountId))) {
            throw new AlreadyParticipantException("Ya esta inscripto");
        }
        if (event.getWaitList().stream().anyMatch(acc -> acc.getUser().getId().equals(accountId))) {
            throw new AlreadyInWaitlistException("Ya esta en la waitlist");
        }

        // --- 2. "RESERVA" ATÓMICA (La Red de Seguridad) ---
        // Creamos la inscripción con estado nulo y la guardamos primero.
        // Si falla (DuplicateKeyException), el Índice Único nos salvó de un doble clic.
        Registration registration = new Registration(event, account, null); // Estado se define luego

        try {
            registrationRepository.save(registration);
        } catch (DuplicateKeyException e) {
            // ¡La red de seguridad funcionó! (Doble clic o race condition)
            // @Transactional hará ROLLBACK de esta transacción vacía.
            throw new AlreadyRegisteredException("El usuario ya está inscripto");
        }

        // --- 3. LÓGICA DE CUPO ATÓMICA (El "Lock" de Cupo) ---
        // Intentamos tomar un cupo Y añadir el participante en un solo paso.
        Query queryCupo = new Query(Criteria.where("_id").is(eventId).and("availableSeats").gt(0));
        Update updateConCupo = new Update()
            .inc("availableSeats", -1)               // Descuenta 1 cupo
            .push("participants", registration);    // Añade la inscripción a la lista

        UpdateResult result = mongoTemplate.updateFirst(queryCupo, updateConCupo, Event.class);

        if (result.getModifiedCount() > 0) {
            // --- 4a. ÉXITO: Había cupo y se tomó ---
            registration.setState(RegistrationState.CONFIRMED);
        } else {
            // --- 4b. FALLO: No había cupo (availableSeats era 0) ---
            // Añadimos a la waitlist atómicamente.
            Query queryWaitlist = new Query(Criteria.where("_id").is(eventId));
            Update updateWaitlist = new Update().push("waitList", registration);

            mongoTemplate.updateFirst(queryWaitlist, updateWaitlist, Event.class);
            registration.setState(RegistrationState.WAITLIST);
        }

        // --- 5. GUARDAR ESTADO FINAL ---
        registrationRepository.save(registration); // Guarda el estado (CONFIRMED o WAITLIST)

        account.addRegistration(registration);
        accountRepository.save(account);

        // Al salir, @Transactional hace COMMIT de todo.
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

    public List<RegistrationDTO> findByUser_IdAndRegistrationState(UUID accountId, RegistrationState registrationState, Integer page, Integer limit) {
        if(page == null || limit == null){
            throw new NullPageInfoException();
        }
        if(registrationState == null){
            return registrationRepository.findByUser_Id(accountId, PageRequest.of(page, limit)).getContent().stream().map(RegistrationDTO::toRegistrationDTO).toList();
        }
        return registrationRepository.findByUser_IdAndCurrentState(accountId, registrationState, PageRequest.of(page, limit)).getContent().stream().map(RegistrationDTO::toRegistrationDTO).toList();
    }

    public List<RegistrationDTO> findByEvent_IdAndRegistrationState(UUID eventId, RegistrationState registrationState, Integer page, Integer limit) {
        if(page == null || limit == null){
            throw new NullPageInfoException();
        }
        if(registrationState == null){
            return registrationRepository.findByEvent_Id(eventId, PageRequest.of(page, limit)).getContent().stream().map(RegistrationDTO::toRegistrationDTO).toList();

        }

        return registrationRepository.findByEvent_IdAndCurrentState(eventId, registrationState, PageRequest.of(page, limit)).getContent().stream().map(RegistrationDTO::toRegistrationDTO).toList();
    }
}
