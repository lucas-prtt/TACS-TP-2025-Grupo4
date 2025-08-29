package org.controllers;

import org.DTOs.EventDTO;
import org.dominio.events.Event;
import org.dominio.events.EventBuilder;
import org.services.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/events")
public class EventController {

    EventService eventService;
    public EventController(EventService eventService){
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
            return ResponseEntity.ok(EventDTO.fromEvent(eventService.crearEvento(eventDTO)));
        } catch (NullPointerException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}