package org.controllers;

import static org.utils.SecurityUtils.checkAccountId;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.DTOs.registrations.RegistrationDTO;
import org.services.OrganizerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<?> getParticipants(@PathVariable("accountId") UUID accountId,
                                             @PathVariable("eventId") UUID eventId,
                                             @RequestParam(name = "page", required = false) Integer page,
                                             @RequestParam(name = "limit", required = false) Integer limit) {

        if (!checkAccountId(accountId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            page = PageNormalizer.normalizeRegistrationsPageNumber(page);
            limit = PageNormalizer.normalizeRegistrationsPageLimit(limit);
            var registrations = organizerService.getRegistrationsFromEvent(accountId, eventId, page, limit)
                .stream()
                .map(registration -> new RegistrationDTO(
                    registration.getId(),
                    registration.getEvent().getId(),
                    registration.getUser().getId(),
                    registration.getEvent().getTitle(),
                    registration.getEvent().getDescription(),
                    registration.getCurrentState(),
                    registration.getDateTime()))
                .toList();

            return ResponseEntity.ok(registrations);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
                .body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{eventId}/waitlist")
    public ResponseEntity<?> getWaitlist(@PathVariable("accountId") UUID accountId,
                                         @PathVariable("eventId") UUID eventId,
                                         @RequestParam(name = "page", required = false) Integer page,
                                         @RequestParam(name = "limit", required = false) Integer limit) {
        if (!checkAccountId(accountId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            page = PageNormalizer.normalizeRegistrationsPageNumber(page);
            limit = PageNormalizer.normalizeRegistrationsPageLimit(limit);
            var waitlist = organizerService.getWaitlistFromEvent(accountId, eventId).stream()
                .map(registration -> new RegistrationDTO(
                    registration.getId(),
                    registration.getEvent().getId(),
                    registration.getUser().getId(),
                    registration.getEvent().getTitle(),
                    registration.getEvent().getDescription(),
                    registration.getCurrentState(),
                    registration.getDateTime()))
                .toList();

            return ResponseEntity.ok(PageSplitter.getPageList(waitlist, page, limit));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
                .body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{eventId}/close")
    public ResponseEntity<?> closeRegistrations(@PathVariable("accountId") UUID accountId,
                                                @PathVariable("eventId") UUID eventId) {
        if(!checkAccountId(accountId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            organizerService.closeRegistrations(accountId, eventId);
            return ResponseEntity.ok("Las inscripciones al evento fueron cerradas correctamente.");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
                .body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}
