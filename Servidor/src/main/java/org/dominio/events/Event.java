package org.dominio.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Getter
@Setter
@AllArgsConstructor
public class Event {
    String title;
    String description;
    LocalDateTime startDateTime;
    Integer durationMinutes;
    String location;
    Integer maxParticipants;
    Optional<Integer> minParticipants;
    BigDecimal price;
    Category category;
    List<Tag> tags;
}
