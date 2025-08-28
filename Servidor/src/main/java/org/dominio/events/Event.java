package org.dominio.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.dominio.usuarios.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

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
    Integer minParticipants; // Puede estar en null para ser opcional
    BigDecimal price;
    Category category;
    List<Tag> tags;
    List<Registration> participants;
    Queue<Account> waitList;
}
