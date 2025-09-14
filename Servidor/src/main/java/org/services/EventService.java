package org.services;

import org.DTOs.events.EventCreateDTO;
import org.DTOs.events.EventDTO;
import org.apache.coyote.BadRequestException;
import org.exceptions.*;
import org.model.events.Event;
import org.model.accounts.Account;
import org.repositories.AccountRepository;
import org.repositories.EventRepository;
import org.repositories.RegistrationRepository;
import org.springframework.stereotype.Service;
import org.model.events.Registration;
import org.utils.PageSplitter;

import java.util.UUID;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import java.util.NoSuchElementException;


@Service
public class EventService {

    private final EventRepository eventRepository;
    private final AccountRepository accountRepository;

    public EventService(EventRepository eventRepository, AccountRepository accountRepository, RegistrationRepository registrationRepository) {
        this.eventRepository = eventRepository;
        this.accountRepository = accountRepository;
    }

    public Event createEvent(EventCreateDTO eventDTO, UUID organizerId) throws AccountNotFoundException, BadRequestException {
        Event newEvent;
        Optional<Account> author;
        try {
            author = accountRepository.findById(String.valueOf(organizerId));
        }catch (Exception e){
            throw new BadRequestException();
        }
        if(author.isEmpty()) throw new AccountNotFoundException("No se encontro el autor con id "+ organizerId.toString());
        try {
        // Crear evento
        newEvent = new Event(
            eventDTO.getTitle(),
            eventDTO.getDescription(),
            eventDTO.getStartDateTime(),
            eventDTO.getDurationMinutes(),
            eventDTO.getLocation(),
            eventDTO.getMaxParticipants(),
            eventDTO.getMinParticipants(),
            eventDTO.getPrice(),
            eventDTO.getCategory(),
            eventDTO.getTags(),
            author.get()
        );
        eventRepository.save(newEvent);
        } catch (Exception e) {
        throw new BadRequestException();
        }
        return newEvent;
    }

    public Event getEvent(UUID eventId) {
        //devuelve el evento (con sus inscriptos y su waitlist)
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        Event event = eventOptional.orElseThrow(() -> new NoSuchElementException("Evento no encontrado con ID: " + eventId));
        return event;
    }

    public EventDTO getEventDTOById(UUID id) throws EventNotFoundException {
        Optional<Event> event = eventRepository.findById(id);
        if (event.isEmpty())
            throw new EventNotFoundException("No se encontró el evento con id " + id);
        return EventDTO.fromEvent(event.get());
    }

    public List<EventDTO> getEventDTOsByQuery(String title, String titleContains, LocalDateTime maxDate, LocalDateTime minDate, String category, List<String> tags, BigDecimal maxPrice, BigDecimal minPrice, Integer page, Integer limit) throws BadRequestException {
        List<Event> events = getEventsByTitleOrContains(title, titleContains);
        List<EventDTO> processedEvents = events.stream()
                .filter(event -> isValidEvent(event, maxDate, minDate, category, tags, maxPrice, minPrice))
                .map(EventDTO::fromEvent)
                .toList();
        return PageSplitter.getPageList(processedEvents, page, limit);
    }
    public List<EventDTO> getEventDTOsByQuery(String title, String titleContains, LocalDateTime maxDate, LocalDateTime minDate, String category, List<String> tags, BigDecimal maxPrice, BigDecimal minPrice) throws BadRequestException {
        return getEventDTOsByQuery( title,  titleContains,  maxDate,  minDate,  category,  tags,  maxPrice,  minPrice);
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



    public List<EventDTO> getEventsByOrganizer(UUID organizerId, Integer page, Integer limit) {
        // Obtiene todos los eventos y filtra los que tienen como organizador al usuario dado
        List<EventDTO> processedEvents =  eventRepository.getAll().stream()
                .filter(event -> event.getOrganizer() != null && event.getOrganizer().getId().equals(organizerId))
                .map(EventDTO::fromEvent)
                .collect(Collectors.toList());
        return PageSplitter.getPageList(processedEvents, page, limit);
    }
    public List<EventDTO> getEventsByOrganizer(UUID organizerId) {
        return getEventsByOrganizer(organizerId, null, null);
    }

    public EventDTO patchEvent(String id, EventDTO eventPatch) {
        Optional<Event> eventOptional = eventRepository.findById(UUID.fromString(id));
        if(eventOptional.isEmpty())
            throw new EventNotFoundException("No se encontro un evento con ese id");
        eventOptional.get().patch(eventPatch);
        eventRepository.save(eventOptional.get());
        return EventDTO.fromEvent(eventOptional.get());
    }
}