package org.controllers;

import static org.utils.SecurityUtils.getCurrentAccountId;

import java.util.UUID;
import org.DTOs.events.EventCreateDTO;
import org.DTOs.events.EventDTO;
import org.DTOs.registrations.RegistrationCreateDTO;
import org.DTOs.registrations.RegistrationDTO;
import org.apache.coyote.BadRequestException;
import org.exceptions.*;
import org.model.events.Registration;
import org.exceptions.AccountNotFoundException;
import org.exceptions.EventNotFoundException;
import org.model.events.Event;
import org.services.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utils.PageNormalizer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Crea un nuevo evento.
     * El organizerId se obtiene del usuario autenticado, no del body.
     */
    @PostMapping
    public ResponseEntity<?> postEvent(@RequestBody EventCreateDTO eventCreateDTO) {
        try {
            UUID id = getCurrentAccountId();
            Event event = eventService.createEvent(eventCreateDTO, id);
            return ResponseEntity.ok(EventDTO.fromEvent(event));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body("Al menos uno de los campos obligatorios del evento es nulo. Se requiere enviar: \n-String title\n-String description\n-LocalDateTime startDateTime\n-Integer durationMinutes\n-String location\n-Integer maxParticipants\n-BigDecimal price\n-UUID organizerId");
        } catch (AccountNotFoundException e){
            return ResponseEntity.badRequest().body("Ningún usuario con el id existe");
        }
    }

    /**
     * Obtiene un evento por su id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable(name = "id") UUID id) {
        try {
            EventDTO eventDTO = eventService.getEventDTOById(id);
            return ResponseEntity.ok(eventDTO);
        } catch (EventNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    // Permite buscar dentro de la lista de eventos aplicando filtros
    // Los filtros admitidos son:
    // title o titleContains (uno solo de los dos. Un string que debe ser o contenerse en el título)
    // maxDate y/o minDate (Las fechas dentro de las cuales puede comenzar el evento)
    // lista de tags que debe incluir el evento
    // categorize
    // precio minimo y precio maxim (>= y <=)
    // todos estos filtros son opcionales. Puede hacerse una consulta sin filtros para obtener todos los eventos
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
            @RequestParam(name = "limit", required = false) Integer limit){
        page = PageNormalizer.normalizeEventsPageNumber(page);
        limit = PageNormalizer.normalizeEventsPageLimit(limit);
        try {
            List<EventDTO> eventsDTO = eventService.getEventDTOsByQuery(
                title, titleContains, maxDate, minDate, category, tags, maxPrice, minPrice, page, limit
            );
            return ResponseEntity.ok(eventsDTO);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Registra al usuario autenticado en un evento.
     * El accountId se obtiene del token, no del body.
     */
    @PostMapping("/registration")
    public ResponseEntity<String> registerUserToEvent(@RequestBody RegistrationCreateDTO registrationCreateDTO) {
    @PostMapping("/registrations")
    public ResponseEntity<?> registerUserToEvent(@RequestBody RegistrationCreateDTO registrationCreateDTO) {
        try {
            UUID accountId = getCurrentAccountId();
            Registration registrationResult = eventService.registerParticipantToEvent(
                registrationCreateDTO.getEventId(),
                accountId
            );
            return ResponseEntity.ok(RegistrationDTO.toRegistrationDTO(registrationResult));
        } catch (EventNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (OrganizerRegisterException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (AlreadyRegisteredException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (EventRegistrationsClosedException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PatchMapping("/{id}")
    public ResponseEntity<EventDTO> patchEvent(@PathVariable(name = "id") String id, @RequestBody EventDTO event) {
        try {
            EventDTO eventDTO = eventService.patchEvent(id, event);
            return ResponseEntity.ok(eventDTO);
        } catch (EventNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
