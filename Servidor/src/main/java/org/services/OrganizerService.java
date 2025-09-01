package org.services;


import org.dominio.events.Event;
import org.dominio.events.Registration;
import org.dominio.usuarios.Account;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.UUID;

@Service
public class OrganizerService {


    private final EventService eventService;

    public OrganizerService(EventService eventService) {
        this.eventService = eventService;
    }

    public List<Account> getRegistrationsFromEvent(UUID eventId) {
        Event event = eventService.getEvent(eventId);
        return event.getParticipants().stream().map(Registration::getUser).toList();
    }

    public Queue<Registration> getWaitlistFromEvent(UUID eventId) {
        Event event = eventService.getEvent(eventId);
        return event.getWaitList();
    }

    public void closeRegistrations(UUID eventId) {
        Event event = eventService.getEvent(eventId);
        event.closeRegistrations();

    }

}
