package org.dominio.events;

import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Accessors(chain = true)
public class EventBuilder {
    @Setter private String title;
    @Setter private String description;
    @Setter private LocalDateTime startDateTime;
    @Setter private Integer durationMinutes;
    @Setter private String location;
    @Setter private Integer maxParticipants;
    @Setter private Integer minParticipants;
    @Setter private BigDecimal price;
    @Setter private Category category;
    @Setter private List<Tag> tags;

    // Construye y devuelve un Event. Requiere antes settear al menos los siguientes campos:
    // String title, String description, LocalDateTime startDateTime, Integer durationMinutes, String location, Integer maxParticipants, BigDecimal price
    public Event build() throws NullPointerException{
        return new Event(title, description, startDateTime, durationMinutes, location, maxParticipants, minParticipants, price, category, tags);
    }


}

