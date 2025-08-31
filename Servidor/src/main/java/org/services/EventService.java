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

    EventRepository eventRepository;
    AccountRepository accountRepository;

    public EventService(EventRepository eventRepository, AccountRepository accountRepository) {
        this.eventRepository = eventRepository;
        this.accountRepository = accountRepository;
    }

    // Crea un evento a partir de un EventDTO
    // Autogenera el uuid, ignorando uno si está dado en eventDTO
    // Tira NullPointerException si falta uno de los valores obligatorios para eventos
    // Devuelve el evento creado, una vez almacenado en el repositorio
    public Event createEvent(EventDTO eventDTO) throws NullPointerException {
        Event newEvent = new Event(eventDTO.getTitle(), eventDTO.getDescription(), eventDTO.getStartDateTime(), eventDTO.getDurationMinutes(), eventDTO.getLocation(), eventDTO.getMaxParticipants(), eventDTO.getMinParticipants(), eventDTO.getPrice(), eventDTO.getCategory(), eventDTO.getTags());
        eventRepository.save(newEvent);
        return newEvent;
    }

    // Dado un UUID, devuelve el EventDTO con el mismo id, o lanza EventNotFoundException si no lo encuentra
    public EventDTO getEventDTOById(String id) throws EventNotFoundException {
        Optional<Event> event = eventRepository.findById(UUID.fromString(id));
        if (event.isEmpty())
            throw new EventNotFoundException("No se encontro el evento con id" + id);
        return EventDTO.fromEvent(event.get());
    }

    // Dada una serie de parámetros opcionales (pueden ser null), ejecuta una búsqueda entre los eventos y devuelve los que coinciden con los parámetros como DTO
    // Los parámetros title y titleContains no pueden ser ambos distintos de null
    // Si ningún parametro está presente, se devuelve una lista de todos los eventos conocidos
    public List<EventDTO> getEventDTOsByQuery(String title, String titleContains, LocalDateTime maxDate, LocalDateTime minDate, String category, List<String> tags, BigDecimal maxPrice, BigDecimal minPrice) throws BadRequestException {
        List<Event> events = getEventsByTitleOrContains(title, titleContains);
        return events.stream().filter(event -> isValidEvent(event, maxDate, minDate, category, tags, maxPrice, minPrice)).map(EventDTO::fromEvent).toList();
    }

    // Recibe String title y String titleContains
    // Solo uno puede ser distinto a null. Si ninguno es null, lanza BadRequestException
    // Si ambos son null devuelve la lista entera
    // Si title es null, devuelve todos los eventos que tienen dentro del título el substring titleContains
    // Si titleContains es null, devuelve todos los eventos que tengan como título, el string title.
    private List<Event> getEventsByTitleOrContains(String title, String titleContains) throws BadRequestException {
        if (title != null && titleContains != null) {
            throw new BadRequestException("No puede haber titleContains y title simultaneamente");
        }
        if (title != null) {
            return getEventsByTitle(title);
        } else if (titleContains != null) {
            return getEventsByTitleContains(titleContains);
        } else {
            return getAllEvents();
        }
    }

    // Devuelve todos los eventos con un título igual a title
    public List<Event> getEventsByTitle(String title) {
        return eventRepository.findByTitle(title);
    }

    // Devuelve todos los eventos que en su título contienen titleContains
    public List<Event> getEventsByTitleContains(String titleContains) {
        return eventRepository.findByTitleContains(titleContains);
    }

    // Devuelve todos los eventos conocidos
    public List<Event> getAllEvents() {
        return eventRepository.getAll();
    }
    // Devuelve válido si el evento cumple las condiciones (Los tags deben incluirse todos en el hecho)
    // Admite parámetros nulos, pero el evento debe ser dado si o sí.
    // MinPrice y MaxPrice se consideran válidos, incluso si el precio es igual a uno de estos dos.
    public Boolean isValidEvent(Event event, LocalDateTime maxDate, LocalDateTime minDate, String category, List<String> tags, BigDecimal maxPrice, BigDecimal minPrice){
        return (
                (maxDate == null || event.getStartDateTime().isBefore(maxDate)) &&
                (minDate == null || event.getStartDateTime().isAfter(minDate)) &&
                (category == null  || (event.getCategory() != null && Objects.equals(event.getCategory().getTitle(), category))) &&
                ((tags == null || tags.isEmpty()) ||
                    tags.stream().allMatch(comparedTagAsString -> event.getTags().stream().anyMatch(tag-> Objects.equals(tag.getNombre(), comparedTagAsString))))
                ) &&
                (maxPrice == null || event.getPrice().compareTo(maxPrice) <= 0) &&
                (minPrice == null || event.getPrice().compareTo(minPrice) >= 0)
                ;
    }

    /**
     * Registra un usuario a un evento. Si hay cupo, lo agrega como participante.
     * Si no hay cupo, lo agrega a la waitlist.
     * @param eventId UUID del evento
     * @param accountId UUID del usuario
     * @return "CONFIRMED" si quedó inscripto, "WAITLIST" si quedó en espera
     * @throws EventNotFoundException si el evento no existe
     */
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
            account.getRegistrations().add(registration); // Relación inversa
            return "CONFIRMED";
        } else {
            event.getWaitList().add(account);
            account.getWaitlists().add(event); // Relación inversa
            return "WAITLIST";
        }
    }

}
