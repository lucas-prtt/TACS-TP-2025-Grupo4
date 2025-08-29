package org.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dominio.events.Category;
import org.dominio.events.Event;
import org.dominio.events.EventBuilder;
import org.dominio.events.Tag;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class EventDTO {
    UUID id;
    String title;
    String description;
    LocalDateTime startDateTime;
    Integer durationMinutes;
    String location;
    Integer maxParticipants;
    Integer minParticipants;
    BigDecimal price;
    Category category;
    List<Tag> tags;


    // Crea un EventDTO a partir de un Event
    // Algunos elementos se comparten con el Event original, por lo que es necesario NO MODIFICAR los valores una vez creado
    public static EventDTO fromEvent(Event event){
        return new EventDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getStartDateTime(),
                event.getDurationMinutes(),
                event.getLocation(),
                event.getMaxParticipants(),
                event.getMinParticipants(),
                event.getPrice(),
                event.getCategory(),
                new ArrayList<>(event.getTags())
        );
    }
}
