package org.controllers;

import static org.utils.SecurityUtils.getCurrentAccountId;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.http.HttpServletResponse;
import org.DTOs.events.EventCreateDTO;
import org.DTOs.events.EventDTO;
import org.DTOs.registrations.RegistrationDTO;
import org.apache.coyote.BadRequestException;
import org.exceptions.*;
import org.model.enums.RegistrationState;
import org.model.events.Registration;
import org.exceptions.AccountNotFoundException;
import org.exceptions.EventNotFoundException;
import org.model.events.Event;
import org.services.EventService;
import org.services.OrganizerService;
import org.services.RegistrationService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utils.ConfigManager;
import org.utils.PageNormalizer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final OrganizerService organizerService;
    private final RegistrationService registrationService;
    public EventController(EventService eventService, OrganizerService organizerService, RegistrationService registrationService) {
        this.eventService = eventService;
        this.organizerService = organizerService;
        this.registrationService = registrationService;
    }

    /**
     * Crea un nuevo evento. El organizerId se obtiene del usuario autenticado.
     * @param eventCreateDTO DTO con los datos del evento a crear
     * @return ResponseEntity con el evento creado o error
     */
    @PostMapping
    public ResponseEntity<?> postEvent(@RequestBody EventCreateDTO eventCreateDTO,  @RequestHeader(name = "Accept-Language", required = false) String lang) {
            eventCreateDTO.validate(lang);
            UUID id = getCurrentAccountId();
            Event event = eventService.createEvent(eventCreateDTO, id);
            return ResponseEntity.ok(EventDTO.fromEvent(event));
    }

    /**
     * Obtiene los eventos organizados por el usuario autenticado, con paginación.
     * @param page Número de página (opcional)
     * @param limit Cantidad de elementos por página (opcional)
     * @return ResponseEntity con la lista de eventos organizados
     */
    @GetMapping("/organized-events")
    public ResponseEntity<List<EventDTO>> getOrganizedEvents(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "limit", required = false) Integer limit) {
        page = PageNormalizer.normalizeEventsPageNumber(page);
        limit = PageNormalizer.normalizeEventsPageLimit(limit);
        UUID id = getCurrentAccountId();
        List<EventDTO> events;
        events = eventService.getEventsByOrganizer(id, page, limit);
        return ResponseEntity.ok(events);
    }

    /**
     * Obtiene un evento por su ID.
     * @param id ID del evento
     * @return ResponseEntity con el evento o error si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable(name = "id") UUID id, @RequestHeader(name = "Accept-Language", required = false) String lang) {
        EventDTO eventDTO = eventService.getEventDTOById(id);
        return ResponseEntity.ok(eventDTO);
    }


    /**
     * Busca eventos aplicando filtros opcionales como título, fechas, categoría, tags y precio.
     * Todos los filtros son opcionales y se puede obtener la lista completa de eventos.
     * @param title Título exacto del evento (opcional)
     * @param titleContains Subcadena que debe estar en el título (opcional)
     * @param maxDate Fecha máxima de inicio (opcional)
     * @param minDate Fecha mínima de inicio (opcional)
     * @param category Categoría del evento (opcional)
     * @param tags Lista de tags (opcional)
     * @param maxPrice Precio máximo (opcional)
     * @param minPrice Precio mínimo (opcional)
     * @param page Número de página (opcional)
     * @param limit Cantidad de elementos por página (opcional)
     * @return ResponseEntity con la lista de eventos filtrados
     */
    @GetMapping
    public ResponseEntity<List<EventDTO>> getEventsByParams(
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "titleContains", required = false) String titleContains,
            @RequestParam(name = "maxDate", required = false) LocalDateTime maxDate,
            @RequestParam(name = "minDate", required = false) LocalDateTime minDate,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "tags", required = false) List<String> tags,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "limit", required = false) Integer limit) throws BadRequestException {
        page = PageNormalizer.normalizeEventsPageNumber(page);
        limit = PageNormalizer.normalizeEventsPageLimit(limit);
        List<EventDTO> eventsDTO = eventService.getEventDTOsByQuery(
            title, titleContains, maxDate, minDate, category, tags, maxPrice, minPrice, page, limit
        );
        return ResponseEntity.ok(eventsDTO);
    }

    /**
     * Registra al usuario autenticado en el evento indicado por su ID.
     * El accountId se obtiene del token.
     * @param eventId ID del evento
     * @return ResponseEntity con la inscripción realizada o error
     */
    @PostMapping("{id}/registrations")
    public ResponseEntity<?> registerUserToEvent(@PathVariable(name = "id") UUID eventId) {
        UUID accountId = getCurrentAccountId();
        Registration registrationResult = registrationService.registerParticipantToEvent(
            eventId,
            accountId
        );
        return ResponseEntity.ok(RegistrationDTO.toRegistrationDTO(registrationResult));
    }
    /**
     * Actualiza parcialmente los datos de un evento por su ID.
     * @param id ID del evento
     * @param event DTO con los datos a actualizar
     * @return ResponseEntity con el evento actualizado o error si no existe
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> patchEvent(@PathVariable(name = "id") String id, @RequestBody EventDTO event) {
            EventDTO eventDTO = eventService.patchEvent(id, event);
            return ResponseEntity.ok(eventDTO);
    }
    /**
     * Obtiene la lista de participantes de un evento, con paginación y filtrado por tipo de inscripción.
     * @param eventId ID del evento
     * @param page Número de página (opcional)
     * @param limit Cantidad de elementos por página (opcional)
     * @param registrationState Tipo de inscripción (opcional)
     * @return ResponseEntity con la lista de inscripciones o error
     */
    @GetMapping("/{eventId}/registrations")
    public ResponseEntity<?> getParticipants(@PathVariable("eventId") UUID eventId,
                                             @RequestParam(name = "page", required = false) Integer page,
                                             @RequestParam(name = "limit", required = false) Integer limit,
                                             @RequestParam(name = "registrationType", required = false) RegistrationState registrationState) {
            page = PageNormalizer.normalizeRegistrationsPageNumber(page);
            limit = PageNormalizer.normalizeRegistrationsPageLimit(limit);
            List<RegistrationDTO> registrationDTOS = registrationService.findByEvent_IdAndRegistrationState(eventId, registrationState, page, limit);

            return ResponseEntity.ok(registrationDTOS);
    }
}