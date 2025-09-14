package org.controllers;

import static org.utils.SecurityUtils.getCurrentAccountId;

import java.util.UUID;
import org.DTOs.events.EventCreateDTO;
import org.DTOs.events.EventDTO;
import org.DTOs.registrations.RegistrationCreateDTO;
import org.apache.coyote.BadRequestException;
import org.exceptions.AccountNotFoundException;
import org.exceptions.EventNotFoundException;
import org.model.events.Event;
import org.services.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        } catch (NullPointerException e) {
            return ResponseEntity.badRequest().body("Campos obligatorios faltantes en el evento");
        } catch (AccountNotFoundException e) {
            return ResponseEntity.badRequest().body("El usuario autenticado no existe");
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

    /**
     * Filtra eventos con distintos parámetros.
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
        @RequestParam(name = "limit", required = false) Integer limit) {
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
        try {
            UUID accountId = getCurrentAccountId();
            String result = eventService.registerParticipantToEvent(
                registrationCreateDTO.getEventId(),
                accountId
            );

            return switch (result) {
                case "ORGANIZER_CANNOT_REGISTER" -> ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
                case "ALREADY_REGISTERED", "ALREADY_IN_WAITLIST" -> ResponseEntity.status(HttpStatus.CONFLICT).body(result);
                default -> ResponseEntity.ok(result);
            };
        } catch (EventNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
