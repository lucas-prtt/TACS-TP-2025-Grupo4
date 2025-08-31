package org.controllers;

import org.dominio.usuarios.Account;
import org.services.OrganizerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Queue;
import java.util.UUID;

@RestController
@RequestMapping("/organizer")
public class OrganizerController {

        private final OrganizerService organizerService;

        public OrganizerController(OrganizerService organizerService) {
            this.organizerService = organizerService;
        }

        @GetMapping("/event/{eventId}/participants")
        public List<Account> getParticipants(@PathVariable UUID eventId) {
            return organizerService.getRegistrationsFromEvent(eventId);
        }

        @GetMapping("/event/{eventId}/waitlist")
        public Queue<Account> getWaitlist(@PathVariable UUID eventId) {
            return organizerService.getWaitlistFromEvent(eventId);
        }

        @PostMapping("/event/{eventId}/close")
        public void closeRegistrations(@PathVariable UUID eventId) {
            organizerService.closeRegistrations(eventId);
        }

}
