package org.services;


import org.model.enums.RegistrationState;
import org.model.events.Event;
import org.model.events.Registration;
import org.springframework.stereotype.Service;
import org.utils.PageSplitter;

import java.util.List;
import java.util.Queue;
import java.util.UUID;

@Service
public class OrganizerService {

    private final EventService eventService;

    public OrganizerService(EventService eventService) {
        this.eventService = eventService;
    }

    private void validateOrganizer(UUID accountId, Event event) {
        if (!event.getOrganizer().getId().equals(accountId)) {
            throw new SecurityException("No tienes permisos para gestionar este evento.");
        }
    }

    public List<Registration> getRegistrationsFromEvent(UUID eventId, RegistrationState state, Integer page, Integer limit) {
        Event event = eventService.getEvent(eventId);
        return PageSplitter.getPageList(event.getParticipants().stream().filter(registration -> state == null || registration.getCurrentState() == state).toList(), page, limit);
    }

    public Queue<Registration> getWaitlistFromEvent(UUID accountId, UUID eventId) {
        Event event = eventService.getEvent(eventId);
        validateOrganizer(accountId, event);
        return event.getWaitList();
    }

    public void closeRegistrations(UUID accountId, UUID eventId) {
        Event event = eventService.getEvent(eventId);
        validateOrganizer(accountId, event);
        event.closeRegistrations();
    }
}
