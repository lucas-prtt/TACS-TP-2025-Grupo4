package org.services;

import org.DTOs.EventDTO;
import org.dominio.events.Event;
import org.dominio.events.EventBuilder;
import org.repositories.EventRepository;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    EventRepository eventRepository;
    public EventService(EventRepository eventRepository){
        this.eventRepository = eventRepository;
    }

    public Event crearEvento(EventDTO eventDTO) throws NullPointerException{
        Event newEvent = new Event(eventDTO.getTitle(), eventDTO.getDescription(), eventDTO.getStartDateTime(), eventDTO.getDurationMinutes(), eventDTO.getLocation(), eventDTO.getMaxParticipants(), eventDTO.getMinParticipants(), eventDTO.getPrice(), eventDTO.getCategory(), eventDTO.getTags());
        eventRepository.save(newEvent);
        return newEvent;
    }

}
