package org.controllers;

import org.DTOs.registrations.RegistrationDTO;
import org.services.OrganizerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utils.PageNormalizer;
import org.utils.PageSplitter;

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
    public List<RegistrationDTO> getParticipants(@PathVariable("eventId") UUID eventId,
                                                 @RequestParam(name = "page", required = false) Integer page,
                                                 @RequestParam(name = "limit", required = false) Integer limit) {
        page = PageNormalizer.normalizeRegistrationsPageNumber(page);
        limit = PageNormalizer.normalizeRegistrationsPageLimit(limit);
        return organizerService.getRegistrationsFromEvent(eventId, page, limit).stream().map(registration -> new RegistrationDTO(registration.getId(),registration.getEvent().getId(), registration.getUser().getId(),registration.getEvent().getTitle(),registration.getEvent().getDescription(), registration.getCurrentState(), registration.getDateTime())).toList();
    }

    @GetMapping("/{eventId}/waitlist")
    public List<RegistrationDTO> getWaitlist(@PathVariable("eventId") UUID eventId,
                                             @RequestParam(name = "page", required = false) Integer page,
                                             @RequestParam(name = "limit", required = false) Integer limit) {
        page = PageNormalizer.normalizeRegistrationsPageNumber(page);
        limit = PageNormalizer.normalizeRegistrationsPageLimit(limit);
        return PageSplitter.getPageList(organizerService.getWaitlistFromEvent(eventId).stream().map(registration -> new RegistrationDTO(registration.getId(),registration.getEvent().getId(), registration.getUser().getId(),registration.getEvent().getTitle(),registration.getEvent().getDescription(), registration.getCurrentState(), registration.getDateTime())).toList(), page, limit);
    }

    @PostMapping("/{eventId}/close")
    public ResponseEntity<String> closeRegistrations(@PathVariable("eventId") UUID eventId) {
        organizerService.closeRegistrations(eventId);
        return ResponseEntity.ok("Las inscripciones al evento fueron cerradas correctamente.");
    }


}
