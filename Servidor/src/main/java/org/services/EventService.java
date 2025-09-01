package org.services;

import org.DTOs.EventDTO;
import org.apache.coyote.BadRequestException;
import org.dominio.events.Event;
import org.dominio.usuarios.Account;
import org.exceptions.EventNotFoundException;
import org.repositories.AccountRepository;
import org.repositories.EventRepository;
import org.springframework.stereotype.Service;
import org.dominio.events.Registration;
import java.util.UUID;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final AccountRepository accountRepository;

    public EventService(EventRepository eventRepository, AccountRepository accountRepository, StatsService statsService) {
        this.eventRepository = eventRepository;
        this.accountRepository = accountRepository;
    }

    public Event createEvent(EventDTO eventDTO) throws NullPointerException {
        Event newEvent = new Event(
                eventDTO.getTitle(),
                eventDTO.getDescription(),
                eventDTO.getStartDateTime(),
                eventDTO.getDurationMinutes(),
                eventDTO.getLocation(),
                eventDTO.getMaxParticipants(),
                eventDTO.getMinParticipants(),
                eventDTO.getPrice(),
                eventDTO.getCategory(),
                eventDTO.getTags()
        );
        eventRepository.save(newEvent);
        return newEvent;
    }

    public EventDTO getEventDTOById(String id) throws EventNotFoundException {
        Optional<Event> event = eventRepository.findById(UUID.fromString(id));
        if (event.isEmpty())
            throw new EventNotFoundException("No se encontró el evento con id " + id);
        return EventDTO.fromEvent(event.get());
    }

    public List<EventDTO> getEventDTOsByQuery(String title, String titleContains, LocalDateTime maxDate, LocalDateTime minDate, String category, List<String> tags, BigDecimal maxPrice, BigDecimal minPrice) throws BadRequestException {
        List<Event> events = getEventsByTitleOrContains(title, titleContains);
        return events.stream()
                .filter(event -> isValidEvent(event, maxDate, minDate, category, tags, maxPrice, minPrice))
                .map(EventDTO::fromEvent)
                .toList();
    }

    private List<Event> getEventsByTitleOrContains(String title, String titleContains) throws BadRequestException {
        if (title != null && titleContains != null) {
            throw new BadRequestException("No puede haber titleContains y title simultáneamente");
        }
        if (title != null) {
            return getEventsByTitle(title);
        } else if (titleContains != null) {
            return getEventsByTitleContains(titleContains);
        } else {
            return getAllEvents();
        }
    }

    public List<Event> getEventsByTitle(String title) {
        return eventRepository.findByTitle(title);
    }

    public List<Event> getEventsByTitleContains(String titleContains) {
        return eventRepository.findByTitleContains(titleContains);
    }

    public List<Event> getAllEvents() {
        return eventRepository.getAll();
    }

    public Boolean isValidEvent(Event event, LocalDateTime maxDate, LocalDateTime minDate, String category, List<String> tags, BigDecimal maxPrice, BigDecimal minPrice) {
        return (
                (maxDate == null || event.getStartDateTime().isBefore(maxDate)) &&
                        (minDate == null || event.getStartDateTime().isAfter(minDate)) &&
                        (category == null || (event.getCategory() != null && Objects.equals(event.getCategory().getTitle(), category))) &&
                        ((tags == null || tags.isEmpty()) ||
                                tags.stream().allMatch(comparedTagAsString ->
                                        event.getTags().stream().anyMatch(tag -> Objects.equals(tag.getNombre(), comparedTagAsString))
                                )
                        ) &&
                        (maxPrice == null || event.getPrice().compareTo(maxPrice) <= 0) &&
                        (minPrice == null || event.getPrice().compareTo(minPrice) >= 0)
        );
    }

    public String registerParticipantToEvent(UUID eventId, UUID accountId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Evento no encontrado"));
        Account account = accountRepository.findById(String.valueOf(accountId))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Registration registration = new Registration();
        registration.setEvent(event);
        registration.setUser(account);

        if (event.getParticipants().size() < event.getMaxParticipants()) {
            event.getParticipants().add(registration);
            account.getRegistrations().add(registration);
            return "CONFIRMED";
        } else {
            event.getWaitList().add(account);
            account.getWaitlists().add(event);
            return "WAITLIST";
        }
    }
}