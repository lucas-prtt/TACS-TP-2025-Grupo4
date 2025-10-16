package org.services;

import org.DTOs.events.EventCreateDTO;
import org.DTOs.events.EventDTO;
import org.apache.coyote.BadRequestException;
import org.exceptions.*;
import org.model.events.Event;
import org.model.accounts.Account;
import org.repositories.AccountRepository;
import org.repositories.EventRepository;
import org.repositories.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.utils.Validator;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final AccountRepository accountRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    public EventService(EventRepository eventRepository, AccountRepository accountRepository, RegistrationRepository registrationRepository) {
        this.eventRepository = eventRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * Crea un nuevo evento a partir de los datos recibidos y el ID del organizador.
     * @param eventDTO DTO con los datos del evento
     * @param organizerId ID del organizador
     * @return El evento creado
     * @throws AccountNotFoundException si el organizador no existe
     * @throws BadRequestException si los datos son inválidos
     */
    public Event createEvent(EventCreateDTO eventDTO, UUID organizerId) throws AccountNotFoundException, InvalidEventUrlException{
        Event newEvent;
        Optional<Account> author;
        author = accountRepository.findById(organizerId);
        if(author.isEmpty()) throw new AccountNotFoundException("No se encontro el autor con id "+ organizerId.toString());
        if(eventDTO.getImage() != null && !Validator.isValidUrlNonLocalhost(eventDTO.getImage()))
            throw new InvalidEventUrlException("La url de la imagen no es valida.");

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
            criteriaList.add(Criteria.where("category").is(category));
        }

        if (tags != null && !tags.isEmpty()) {
            criteriaList.add(Criteria.where("tags").all(tags));
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
     * @param id ID del evento
     * @param eventPatch DTO con los datos a actualizar
     * @return DTO del evento actualizado
     * @throws EventNotFoundException si no existe el evento
     */
    public EventDTO patchEvent(String id, EventDTO eventPatch) {
        Optional<Event> eventOptional = eventRepository.findById(UUID.fromString(id));
        if(eventOptional.isEmpty())
            throw new EventNotFoundException("No se encontro un evento con ese id");

        Event event = eventOptional.get();
        // Validar que el usuario autenticado sea el organizador
        UUID currentUserId = org.utils.SecurityUtils.getCurrentAccountId();
        if (event.getOrganizer() == null || event.getOrganizer().getId() == null || !event.getOrganizer().getId().equals(currentUserId)) {
            throw new SecurityException("Solo el organizador puede modificar este evento");
        }

        event.patch(eventPatch);
        eventRepository.save(event);
        return EventDTO.fromEvent(event);
    }
}