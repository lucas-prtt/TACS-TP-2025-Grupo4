package org.services;

import org.DTOs.events.EventCreateDTO;
import org.DTOs.events.EventDTO;
import org.apache.coyote.BadRequestException;
import org.exceptions.*;
import org.model.enums.EventState;
import org.model.enums.RegistrationState;
import org.model.events.Event;
import org.model.accounts.Account;
import org.model.events.Registration;
import org.repositories.AccountRepository;
import org.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

import org.springframework.data.mongodb.core.query.Update;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final AccountRepository accountRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public EventService(EventRepository eventRepository,AccountRepository accountRepository, MongoTemplate mongoTemplate) {
        this.eventRepository = eventRepository;
        this.accountRepository = accountRepository;
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Crea un nuevo evento a partir de los datos recibidos y el ID del organizador.
     * @param eventDTO DTO con los datos del evento
     * @param organizerId ID del organizador
     * @return El evento creado
     * @throws AccountNotFoundException si el organizador no existe
     * @throws InvalidEventUrlException si los datos son inválidos
     */
    public Event createEvent(EventCreateDTO eventDTO, UUID organizerId) throws AccountNotFoundException, InvalidEventUrlException{
        Event newEvent;
        Optional<Account> author;
        author = accountRepository.findById(organizerId);
        if(author.isEmpty()) throw new AccountNotFoundException();


        newEvent = new Event(
            eventDTO.getTitle(),
            eventDTO.getDescription(),
            eventDTO.getStartDateTime(),
            eventDTO.getDurationMinutes(),
            eventDTO.getLocation(),
            eventDTO.getMaxParticipants(),
            eventDTO.getMinParticipants(),
            eventDTO.getPrice(),
            eventDTO.getCategory(),
            eventDTO.getTags(),
            author.get(),
            eventDTO.getImage()
        );
        eventRepository.save(newEvent);
        return newEvent;
    }

    /**
     * Devuelve el evento por su ID, incluyendo inscriptos y lista de espera.
     * @param eventId ID del evento
     * @return El evento encontrado
     * @throws NoSuchElementException si no existe el evento
     */
    public Event getEvent(UUID eventId) {
        return eventRepository.findById(eventId).
                orElseThrow(() -> new NoSuchElementException("Evento no encontrado con ID dada"));
    }

    /**
     * Devuelve el DTO de un evento por su ID.
     * @param id ID del evento
     * @return DTO del evento
     * @throws EventNotFoundException si no existe el evento
     */
    public EventDTO getEventDTOById(UUID id) throws EventNotFoundException {
        Optional<Event> event = eventRepository.findById(id);
        if (event.isEmpty())
            throw new EventNotFoundException("No se encontró el evento con id " + id);
        return EventDTO.fromEvent(event.get());
    }

    /**
     * Busca eventos aplicando filtros y devuelve una lista paginada de DTOs.
     * @param title Título exacto (opcional)
     * @param titleContains Subcadena en el título (opcional)
     * @param maxDate Fecha máxima (opcional)
     * @param minDate Fecha mínima (opcional)
     * @param category Categoría (opcional)
     * @param tags Lista de tags (opcional)
     * @param maxPrice Precio máximo (opcional)
     * @param minPrice Precio mínimo (opcional)
     * @param page Página (opcional)
     * @param limit Límite (opcional)
     * @return Lista paginada de eventos
     * @throws BadRequestException si los filtros son inválidos
     */
    public List<EventDTO> getEventDTOsByQuery(
            String title, String titleContains,
            LocalDateTime maxDate, LocalDateTime minDate,
            String category, List<String> tags,
            BigDecimal maxPrice, BigDecimal minPrice,
            Integer page, Integer limit) throws BadRequestException {

        if(page == null || limit == null){
            throw  new NullPageInfoException();
        }

        List<Criteria> criteriaList = new ArrayList<>();

        if (title != null && titleContains != null) {
            throw new BadRequestException("No puede haber titleContains y title simultáneamente");
        }

        if (title != null) {
            criteriaList.add(Criteria.where("title").is(title));
        } else if (titleContains != null) {
            criteriaList.add(Criteria.where("title").regex(".*" + Pattern.quote(titleContains) + ".*", "i"));
        }

        if (minDate != null) {
            criteriaList.add(Criteria.where("startDate").gte(minDate));
        }
        if (maxDate != null) {
            criteriaList.add(Criteria.where("startDate").lte(maxDate));
        }

        if (category != null) {
            criteriaList.add(Criteria.where("category.title").is(category));
        }

        if (tags != null && !tags.isEmpty()) {
            criteriaList.add(Criteria.where("tags.title").all(tags));
        }

        if (minPrice != null) {
            criteriaList.add(Criteria.where("price").gte(minPrice));
        }
        if (maxPrice != null) {
            criteriaList.add(Criteria.where("price").lte(maxPrice));
        }

        Query query = new Query();

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        query.with(PageRequest.of(page, limit));

        List<Event> events = mongoTemplate.find(query, Event.class);

        return events.stream()
                .map(EventDTO::fromEvent)
                .toList();
    }

    /**
     * Devuelve todos los eventos.
     * @return Lista de todos los eventos
     */
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }


    /**
     * Obtiene los eventos organizados por el usuario dado, con paginación.
     * @param organizerId ID del organizador
     * @param page Número de página (opcional)
     * @param limit Cantidad de elementos por página (opcional)
     * @return Lista paginada de eventos organizados
     */
    public List<EventDTO> getEventsByOrganizer(UUID organizerId, Integer page, Integer limit) {
        if(page == null || limit == null){
            throw new NullPageInfoException();
        }
        List<Event> organizerEvents =  eventRepository.findByOrganizerId(organizerId, PageRequest.of(page, limit)).getContent();
        return organizerEvents.stream().map(EventDTO::fromEvent).toList();
    }

    /**
     * Actualiza parcialmente los datos de un evento por su ID.
     * @param eventUuid ID del evento
     * @param eventPatch DTO con los datos a actualizar
     * @return DTO del evento actualizado
     * @throws EventNotFoundException si no existe el evento
     */
    @Retryable(retryFor = { TransientDataAccessException.class })
    @Transactional // Garantiza la atomicidad
    public EventDTO patchEvent(UUID eventUuid, EventDTO eventPatch) throws BadRequestException {

        Optional<Event> eventOptional = eventRepository.findById(eventUuid);
        if(eventOptional.isEmpty()) throw new EventNotFoundException("No se encontro un evento con ese id");
        Event event = eventOptional.get();

        UUID currentUserId = org.utils.SecurityUtils.getCurrentAccountId();
        if (event.getOrganizer() == null || !event.getOrganizer().getId().equals(currentUserId)) {
            throw new SecurityException("Solo el organizador puede modificar este evento");
        }

        if (isEventTimePassed(event)) {
            throw new BadRequestException("El evento ha finalizado y no puede ser modificado.");
        }

        EventState originalState = event.getEventState();
        EventState newState = eventPatch.getState();

        // 2. Aplicar el patch al objeto Event en memoria
        event.patch(eventPatch);

        // 3. LÓGICA DE TRANSICIÓN Y ACCIONES NECESARIAS
        if (newState != null && newState != originalState) {
            // validar transición
            if (!isValidTransition(originalState, newState)) {
                throw new BadRequestException("Transición de estado no permitida: " + originalState + " -> " + newState);
            }

            if (newState == EventState.EVENT_CLOSED) {
                handleCloseEvent(event); // Cierra waitlist
            } else if (newState == EventState.EVENT_CANCELLED) {
                handleCancelEvent(event); // Cancela Todo
            } else if (newState == EventState.EVENT_OPEN && originalState == EventState.EVENT_CLOSED) {
                handleReopenEvent(event); // Reabre el evento
            }
        }

        // 4. Guardar el objeto Event modificado
        eventRepository.save(event);
        return EventDTO.fromEvent(event);
    }



    // --- MÉTODOS PRIVADOS DE LÓGICA DE ESTADO Y CONCURRENCIA ---

    /**
     * Valida si la hora actual es posterior a la hora de finalización del evento.
     */
    private boolean isEventTimePassed(Event event) {
        if (event.getStartDateTime() == null || event.getDurationMinutes() == null) {
            return false;
        }
        LocalDateTime eventEndTime = event.getStartDateTime().plusMinutes(event.getDurationMinutes());
        return LocalDateTime.now().isAfter(eventEndTime);
    }

    /**
     * Marca todas las inscripciones activas como CANCELADAS y limpia las listas.
     */
    private void handleCancelEvent(Event event) {
        UUID eventId = event.getId();

        // 1. Operación masiva en la colección 'registrations'
        // Busca CONFIRMED y WAITLIST para asegurar que se cancelen.
        Query queryAllActiveRegs = new Query(Criteria.where("event.$id").is(eventId)
            .orOperator(
                Criteria.where("currentState").is(RegistrationState.CONFIRMED),
                Criteria.where("currentState").is(RegistrationState.WAITLIST)
            )
        );
        Update updateState = new Update().set("currentState", RegistrationState.CANCELED);
        this.mongoTemplate.updateMulti(queryAllActiveRegs, updateState, Registration.class); // APLICA A TODOS

        // 2. Mutar el objeto Event en memoria para el save final (consistencia)
        event.setParticipants(new ArrayList<>());
        event.setWaitList(new ArrayList<>());
        event.setAvailableSeats(event.getMaxParticipants()); // Restablecer cupo
        event.setEventState(EventState.EVENT_CANCELLED);
    }

    /**
     * Lógica para EVENT_CLOSED (Cierre de Inscripción).
     * Solo cancela la WAITLIST.
     */
    private void handleCloseEvent(Event event) {
        UUID eventId = event.getId();

        // 1. Marcar solo la WAITLIST como CANCELLED
        Query queryWaitlistRegs = new Query(
            Criteria.where("event.$id").is(eventId)
                .and("currentState").is(RegistrationState.WAITLIST)
        );
        Update updateState = new Update().set("currentState", RegistrationState.CANCELED);
        this.mongoTemplate.updateMulti(queryWaitlistRegs, updateState, Registration.class);

        // 2. Mutar el objeto Event en memoria: limpiar waitList y setear estado CLOSED.
        event.setWaitList(new ArrayList<>());
        event.setEventState(EventState.EVENT_CLOSED);
    }

    /**
     * Reabre un evento que estaba cerrado.
     * Recalcula availableSeats y pone el estado a EVENT_OPEN.
     */
    private void handleReopenEvent(Event event) {
        // Recalcular el cupo disponible basado en la lista de participantes sincronizada
        int confirmed = (event.getParticipants() == null) ? 0 : event.getParticipants().size();
        int maxParticipants = (event.getMaxParticipants() == null) ? 0 : event.getMaxParticipants();
        event.setAvailableSeats(Math.max(0, maxParticipants - confirmed));
        event.setEventState(EventState.EVENT_OPEN);
    }

    /**
     * Valida si una transición de estados es permitida según reglas de negocio.
     */
    private boolean isValidTransition(EventState from, EventState to) {
        if (from == null || to == null) return false;
        if (from == to) return false;
        // Una vez cancelado, no se permite volver a otro estado
        if (from == EventState.EVENT_CANCELLED) return false;

        return switch (from) {
            case EVENT_OPEN -> to == EventState.EVENT_CLOSED || to == EventState.EVENT_CANCELLED;
            case EVENT_CLOSED -> to == EventState.EVENT_OPEN || to == EventState.EVENT_CANCELLED;
            default -> false;
        };
    }


}
