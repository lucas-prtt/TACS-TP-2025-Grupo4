package org.model.events;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.model.accounts.Account;

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
    @Setter private Account organizer;

    /**
     * Construye y devuelve un objeto Event usando los valores seteados previamente.
     * Requiere que los campos obligatorios estén seteados.
     * @return Nuevo objeto Event
     * @throws NullPointerException si falta algún campo obligatorio
     */
    public Event build() throws NullPointerException{
        return new Event(title, description, startDateTime, durationMinutes, location, maxParticipants, minParticipants, price, category, tags, organizer);
    }


}

