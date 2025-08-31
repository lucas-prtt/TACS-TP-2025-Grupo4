package org.services;

import org.DTOs.EventDTO;
import org.dominio.events.Event;
import org.dominio.events.EventBuilder;
import org.repositories.EventRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventService {

    EventRepository eventRepository;
    public EventService(EventRepository eventRepository){
        this.eventRepository = eventRepository;
    }
    // Crea un evento a partir de un EventDTO
    // Autogenera el uuid, ignorando uno si está dado en eventDTO
    // Tira NullPointerException si falta uno de los valores obligatorios para eventos
    // Devuelve el evento creado, una vez almacenado en el repositorio
    public Event crearEvento(EventDTO eventDTO) throws NullPointerException{
        Event newEvent = new Event(eventDTO.getTitle(), eventDTO.getDescription(), eventDTO.getStartDateTime(), eventDTO.getDurationMinutes(), eventDTO.getLocation(), eventDTO.getMaxParticipants(), eventDTO.getMinParticipants(), eventDTO.getPrice(), eventDTO.getCategory(), eventDTO.getTags());
        eventRepository.save(newEvent);
        return newEvent;
    }
    public Event getEvent(UUID eventId) {
        //devuelve el evento (con sus inscriptos y su waitlist)
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        Event event = eventOptional.orElseThrow(() -> new NoSuchElementException("Evento no encontrado con ID: " + eventId));
        return event;
    }

}
