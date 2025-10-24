package org.services;

import org.DTOs.events.EventDTO;
import org.exceptions.CategoryNotFoundException;
import org.exceptions.EventNotFoundException;
import org.model.events.Event;
import org.repositories.AccountRepository;
import org.repositories.EventRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

public class CategoryService {
    private final EventRepository eventRepository;
    
    public CategoryService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public EventDTO getCategoryById(UUID id) throws CategoryNotFoundException {
        Optional<Event> event = eventRepository.findById(id);
        if (event.isEmpty())
            throw new EventNotFoundException("No se encontró el evento con id " + id);
        return EventDTO.fromEvent(event.get());
    }
}
