package org.controllers;

import org.DTOs.EventDTO;
import org.DTOs.registrations.RegistrationCreateDTO;
import org.DTOs.registrations.RegistrationDTO;
import org.apache.coyote.BadRequestException;
import org.exceptions.*;
import org.model.events.Registration;
import org.services.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utils.PageNormalizer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController()
@RequestMapping("/events")
public class EventController {

    EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // Postea un evento
    // El evento debe tener todos sus campos obligatorios y no tener ID. De lo contrario, devuelve badRequest
    // Si la request puede ser procesada, devuelve el EventDTO con el campo ID completo con el que se autogeneró el evento
    @PostMapping
    public ResponseEntity<?> postEvent(@RequestBody EventDTO eventDTO) {
        if (eventDTO.getId() != null)
            return ResponseEntity.badRequest().build();
        try {
            return ResponseEntity.ok(EventDTO.fromEvent(eventService.createEvent(eventDTO)));
        } catch (NullPointerException e) {
            return ResponseEntity.badRequest().body("Al menos uno de los campos obligatorios del evento es nulo. Se requiere enviar: \n-String title\n-String description\n-LocalDateTime startDateTime\n-Integer durationMinutes\n-String location\n-Integer maxParticipants\n-BigDecimal price\n-UUID organizerId");
        } catch (AccountNotFoundException e){
            return ResponseEntity.badRequest().body("Ningún usuario con el id existe");
        }
    }


    // Obtiene un evento a partir de un uuid en el path
    // Si no se encuentra devuelve 404 NOTFOUND. De lo contrario devuelve el errorDTO
    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable(name = "id") String id) {
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
            List<EventDTO> eventsDTO = eventService.getEventDTOsByQuery(title, titleContains, maxDate, minDate, category, tags, maxPrice, minPrice, page, limit);
            return ResponseEntity.ok(eventsDTO);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/registrations")
    public ResponseEntity<?> registerUserToEvent(@RequestBody RegistrationCreateDTO registrationCreateDTO) {
        try {
            Registration registrationResult= eventService.registerParticipantToEvent(
                registrationCreateDTO.getEventId(),
                registrationCreateDTO.getAccountId()
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