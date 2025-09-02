package org.controllers;

import org.DTOs.RegistrationDTO;
import org.services.OrganizerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.UUID;


@RestController
@RequestMapping("/organizer")
public class OrganizerController {

    private final OrganizerService organizerService;

    public OrganizerController(OrganizerService organizerService) {
        this.organizerService = organizerService;
    }

    @GetMapping("/event/{eventId}/participants")
    public List<RegistrationDTO> getParticipants(@PathVariable("eventId") UUID eventId) {
        return organizerService.getRegistrationsFromEvent(eventId).stream().map(registration -> new RegistrationDTO(registration.getEvent().getId(),registration.getUser().getUuid())).toList();
    }

    @GetMapping("/event/{eventId}/waitlist")
    public List<RegistrationDTO> getWaitlist(@PathVariable("eventId") UUID eventId) {
        return organizerService.getWaitlistFromEvent(eventId).stream().map(registration -> new RegistrationDTO(registration.getEvent().getId(),registration.getUser().getUuid())).toList();
    }

    @PostMapping("/event/{eventId}/close")
    public void closeRegistrations(@PathVariable("eventId") UUID eventId) {
        organizerService.closeRegistrations(eventId);
    }

}
