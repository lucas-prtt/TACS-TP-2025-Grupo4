package org.services;


import org.model.events.Event;
import org.model.events.Registration;
import org.springframework.stereotype.Service;
import org.utils.PageSplitter;

import javax.swing.plaf.InsetsUIResource;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

@Service
public class OrganizerService {


    private final EventService eventService;

    public OrganizerService(EventService eventService) {
        this.eventService = eventService;
    }

    public List<Registration> getRegistrationsFromEvent(UUID eventId, Integer page, Integer limit) {
        Event event = eventService.getEvent(eventId);
        return PageSplitter.getPageList(event.getParticipants(), page, limit);
    }
    public List<Registration> getRegistrationsFromEvent(UUID eventId) {
        return getRegistrationsFromEvent(eventId, null, null);
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
