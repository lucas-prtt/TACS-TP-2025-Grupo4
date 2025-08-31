package org.controllers;

import org.DTOs.EventDTO;
import org.apache.coyote.BadRequestException;
import org.dominio.usuarios.Account;
import org.exceptions.EventNotFoundException;
import org.services.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
    public ResponseEntity<EventDTO> postEvent(@RequestBody EventDTO eventDTO) {
        if (eventDTO.getId() != null)
            return ResponseEntity.badRequest().build();
        try {
            return ResponseEntity.ok(EventDTO.fromEvent(eventService.createEvent(eventDTO)));
        } catch (NullPointerException e) {
            return ResponseEntity.badRequest().build();
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
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice){
        try {
            List<EventDTO> eventsDTO = eventService.getEventDTOsByQuery(title, titleContains, maxDate, minDate, category, tags, maxPrice, minPrice);
            return ResponseEntity.ok(eventsDTO);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/register")
    public ResponseEntity<String> registerUserToEvent(
            @PathVariable("id") String eventId,
            @RequestBody Account account
    ) {
        try {
            String result = eventService.registerParticipantToEvent(UUID.fromString(eventId), account);
            return ResponseEntity.ok(result);
        } catch (EventNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}