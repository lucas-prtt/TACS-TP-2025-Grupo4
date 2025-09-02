package org.services;


import org.model.events.Event;
import org.model.events.Registration;
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

    public List<Registration> getRegistrationsFromEvent(UUID eventId) {
        Event event = eventService.getEvent(eventId);
        return event.getParticipants();
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
