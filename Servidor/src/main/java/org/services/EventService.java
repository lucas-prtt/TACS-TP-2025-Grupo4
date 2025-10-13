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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.utils.PageSplitter;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final AccountRepository accountRepository;

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
    public Event createEvent(EventCreateDTO eventDTO, UUID organizerId) throws AccountNotFoundException{
        Event newEvent;
        Optional<Account> author;
        author = accountRepository.findById(organizerId);
        if(author.isEmpty()) throw new AccountNotFoundException("No se encontro el autor con id "+ organizerId.toString());
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
            author.get()
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
    public List<EventDTO> getEventDTOsByQuery(String title, String titleContains, LocalDateTime maxDate, LocalDateTime minDate, String category, List<String> tags, BigDecimal maxPrice, BigDecimal minPrice, Integer page, Integer limit) throws BadRequestException {
        List<Event> events = getEventsByTitleOrContains(title, titleContains);
        List<EventDTO> processedEvents = events.stream()
                .filter(event -> isValidEvent(event, maxDate, minDate, category, tags, maxPrice, minPrice))
                .map(EventDTO::fromEvent)
                .toList();
        return PageSplitter.getPageList(processedEvents, page, limit);
    }

    /**
     * Variante de búsqueda de eventos sin paginación.
     * @param title Título exacto (opcional)
     * @param titleContains Subcadena en el título (opcional)
     * @param maxDate Fecha máxima (opcional)
     * @param minDate Fecha mínima (opcional)
     * @param category Categoría (opcional)
     * @param tags Lista de tags (opcional)
     * @param maxPrice Precio máximo (opcional)
     * @param minPrice Precio mínimo (opcional)
     * @return Lista de eventos filtrados
     * @throws BadRequestException si los filtros son inválidos
     */
    public List<EventDTO> getEventDTOsByQuery(String title, String titleContains, LocalDateTime maxDate, LocalDateTime minDate, String category, List<String> tags, BigDecimal maxPrice, BigDecimal minPrice) throws BadRequestException {
        return getEventDTOsByQuery( title,  titleContains,  maxDate,  minDate,  category,  tags,  maxPrice,  minPrice);
    }

    /**
     * Devuelve eventos filtrando por título exacto o por subcadena en el título.
     * @param title Título exacto (opcional)
     * @param titleContains Subcadena en el título (opcional)
     * @return Lista de eventos filtrados
     * @throws BadRequestException si ambos filtros están presentes
     */
    private List<Event> getEventsByTitleOrContains(String title, String titleContains) throws BadRequestException {
        if (title != null && titleContains != null) {
            throw new BadRequestException("No puede haber titleContains y title simultáneamente");
        }
        if (title != null) {
            return getEventsByTitle(title);
        } else if (titleContains != null) {
            return getEventsByTitleContains(titleContains);
        } else {
            return getAllEvents();
        }
    }

    /**
     * Devuelve eventos que coinciden exactamente con el título.
     * @param title Título exacto
     * @return Lista de eventos
     */
    public List<Event> getEventsByTitle(String title) {
        return eventRepository.findByTitle(title);
    }

    /**
     * Devuelve eventos cuyo título contiene la subcadena dada.
     * @param titleContains Subcadena a buscar en el título
     * @return Lista de eventos
     */
    public List<Event> getEventsByTitleContains(String titleContains) {
        return eventRepository.findByTitleContains(titleContains);
    }

    /**
     * Devuelve todos los eventos.
     * @return Lista de todos los eventos
     */
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    /**
     * Verifica si un evento cumple con los filtros dados.
     * @param event Evento a validar
     * @param maxDate Fecha máxima (opcional)
     * @param minDate Fecha mínima (opcional)
     * @param category Categoría (opcional)
     * @param tags Lista de tags (opcional)
     * @param maxPrice Precio máximo (opcional)
     * @param minPrice Precio mínimo (opcional)
     * @return true si el evento cumple los filtros, false si no
     */
    public Boolean isValidEvent(Event event, LocalDateTime maxDate, LocalDateTime minDate, String category, List<String> tags, BigDecimal maxPrice, BigDecimal minPrice) {
        return (
                (maxDate == null || event.getStartDateTime().isBefore(maxDate)) &&
                        (minDate == null || event.getStartDateTime().isAfter(minDate)) &&
                        (category == null || (event.getCategory() != null && Objects.equals(event.getCategory().getTitle(), category))) &&
                        ((tags == null || tags.isEmpty()) ||
                                tags.stream().allMatch(comparedTagAsString ->
                                        event.getTags().stream().anyMatch(tag -> Objects.equals(tag.getNombre(), comparedTagAsString))
                                )
                        ) &&
                        (maxPrice == null || event.getPrice().compareTo(maxPrice) <= 0) &&
                        (minPrice == null || event.getPrice().compareTo(minPrice) >= 0)
        );
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
        List<Event> organizerEvents =  eventRepository.findByOrganizerId(organizerId, PageRequest.of(page, limit));
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