package org.DTOs.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.model.enums.EventState;
import org.model.events.Category;
import org.model.events.Event;
import org.model.events.Tag;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    UUID id;
    String title;
    String description;
    String usernameOrganizer;
    LocalDateTime startDateTime;
    Integer durationMinutes;
    String location;
    Integer maxParticipants;
    Integer minParticipants;
    BigDecimal price;
    Category category;
    List<Tag> tags;
    EventState state;
    Integer registered;
    Integer waitlisted;


    // Crea un EventDTO a partir de un Event
    // Algunos elementos se comparten con el Event original, por lo que es necesario NO MODIFICAR los valores una vez creado
    public static EventDTO fromEvent(Event event){
        return new EventDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getOrganizer().getUsername(),
                event.getStartDateTime(),
                event.getDurationMinutes(),
                event.getLocation(),
                event.getMaxParticipants(),
                event.getMinParticipants(),
                event.getPrice(),
                event.getCategory(),
                new ArrayList<>(event.getTags()),
                event.getEventState(),
                event.getParticipants().size(),
                event.getWaitList().size()
        );
    }
}
