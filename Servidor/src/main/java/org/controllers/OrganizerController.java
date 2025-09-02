package org.controllers;

import org.DTOs.registrations.RegistrationDTO;
import org.services.OrganizerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.UUID;


@RestController
@RequestMapping("accounts/{accountId}/organized-events")
public class OrganizerController {

    private final OrganizerService organizerService;

    public OrganizerController(OrganizerService organizerService) {
        this.organizerService = organizerService;
    }

    @GetMapping("/{eventId}/participants")
    public List<RegistrationDTO> getParticipants(@PathVariable("eventId") UUID eventId) {
        return organizerService.getRegistrationsFromEvent(eventId).stream().map(registration -> new RegistrationDTO(registration.getId(),registration.getEvent().getId(), registration.getUser().getId(),registration.getEvent().getTitle(),registration.getEvent().getDescription(), registration.getCurrentState(), registration.getDateTime())).toList();
    }

    @GetMapping("/{eventId}/waitlist")
    public List<RegistrationDTO> getWaitlist(@PathVariable("eventId") UUID eventId) {
        return organizerService.getWaitlistFromEvent(eventId).stream().map(registration -> new RegistrationDTO(registration.getId(),registration.getEvent().getId(), registration.getUser().getId(),registration.getEvent().getTitle(),registration.getEvent().getDescription(), registration.getCurrentState(), registration.getDateTime())).toList();
    }

    @PostMapping("/{eventId}/close")
    public ResponseEntity<String> closeRegistrations(@PathVariable("eventId") UUID eventId) {
        organizerService.closeRegistrations(eventId);
        return ResponseEntity.ok("Las inscripciones al evento fueron cerradas correctamente.");
    }


}
