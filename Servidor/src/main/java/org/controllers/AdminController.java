package org.controllers;

import static org.utils.SecurityUtils.getCurrentAccountId;

import java.util.UUID;
import org.DTOs.events.EventCreateDTO;
import org.DTOs.events.EventDTO;
import org.DTOs.registrations.RegistrationDTO;
import org.DTOs.StatsDTO;
import org.apache.coyote.BadRequestException;
import org.model.events.Event;
import org.model.events.Registration;
import org.services.EventService;
import org.services.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final StatsService statsService;

    public AdminController(EventService eventService, StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/stats")
    public ResponseEntity<StatsDTO> getStats() {
        return ResponseEntity.ok(statsService.getStats());
    }
}