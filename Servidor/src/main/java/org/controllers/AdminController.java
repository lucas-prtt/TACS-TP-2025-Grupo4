package org.controllers;

import org.DTOs.EventDTO;
import org.DTOs.RegistrationDTO;
import org.DTOs.StatsDTO;
import org.dominio.events.Event;
import org.services.EventService;
import org.services.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final EventService eventService;
    private final StatsService statsService;

    public AdminController(EventService eventService, StatsService statsService) {
        this.eventService = eventService;
        this.statsService = statsService;
    }

    @PostMapping("/event")
    public ResponseEntity<EventDTO> createEvent(@RequestBody EventDTO eventDTO) {
        Event event = eventService.createEvent(eventDTO);
        return ResponseEntity.ok(EventDTO.fromEvent(event));
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegistrationDTO registrationDTO) {
        String status = eventService.registerParticipantToEvent(
                registrationDTO.getEventId(),
                registrationDTO.getAccountId()
        );
        return ResponseEntity.ok(status);
    }

    @GetMapping("/stats")
    public ResponseEntity<StatsDTO> getStats() {
        return ResponseEntity.ok(statsService.getStats());
    }
}