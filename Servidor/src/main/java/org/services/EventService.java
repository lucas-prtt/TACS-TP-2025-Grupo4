package org.services;

import org.DTOs.EventDTO;
import org.apache.coyote.BadRequestException;
import org.dominio.events.Event;
import org.exceptions.EventNotFoundException;
import org.repositories.EventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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
    public Event createEvent(EventDTO eventDTO) throws NullPointerException{
        Event newEvent = new Event(eventDTO.getTitle(), eventDTO.getDescription(), eventDTO.getStartDateTime(), eventDTO.getDurationMinutes(), eventDTO.getLocation(), eventDTO.getMaxParticipants(), eventDTO.getMinParticipants(), eventDTO.getPrice(), eventDTO.getCategory(), eventDTO.getTags());
        eventRepository.save(newEvent);
        return newEvent;
    }

    // Dado un UUID, devuelve el EventDTO con el mismo id, o lanza EventNotFoundException si no lo encuentra
    public EventDTO getEventDTOById(String id) throws EventNotFoundException{
        Optional<Event> event = eventRepository.findById(UUID.fromString(id));
        if(event.isEmpty())
            throw new EventNotFoundException("No se encontro el evento con id" + id);
        return EventDTO.fromEvent(event.get());
    }

    // Dada una serie de parámetros opcionales (pueden ser null), ejecuta una búsqueda entre los eventos y devuelve los que coinciden con los parámetros como DTO
    // Los parámetros title y titleContains no pueden ser ambos distintos de null
    // Si ningún parametro está presente, se devuelve una lista de todos los eventos conocidos
    public List<EventDTO> getEventDTOsByQuery(String title, String titleContains, LocalDateTime maxDate, LocalDateTime minDate) throws BadRequestException {
        List<Event> events = getEventsByTitleOrContains(title, titleContains);
        return events.stream().filter(event ->
                (maxDate == null ||
                        event.getStartDateTime().isBefore(maxDate)) &&
                        (minDate == null ||
                                event.getStartDateTime().isAfter(minDate))
        ).map(EventDTO::fromEvent).toList();
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
    public List<Event> getEventsByTitle(String title){
        return eventRepository.findByTitle(title);
    }
    // Devuelve todos los eventos que en su título contienen titleContains
    public List<Event> getEventsByTitleContains(String titleContains){
        return eventRepository.findByTitleContains(titleContains);
    }
    // Devuelve todos los eventos conocidos
    public List<Event> getAllEvents(){
        return eventRepository.getAll();
    }

}
