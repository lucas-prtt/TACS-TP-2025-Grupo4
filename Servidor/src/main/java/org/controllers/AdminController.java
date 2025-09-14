package org.controllers;

import org.DTOs.EventDTO;
import org.DTOs.registrations.RegistrationDTO;
import org.DTOs.StatsDTO;
import org.model.events.Event;
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

    @GetMapping("/stats")
    public ResponseEntity<StatsDTO> getStats() {
        return ResponseEntity.ok(statsService.getStats());
    }
}