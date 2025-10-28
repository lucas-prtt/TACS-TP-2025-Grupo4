package org.model.events;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.model.enums.RegistrationState;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
public class RegistrationStateChange {
    private UUID id;
    private RegistrationState fromState;
    private RegistrationState toState;
    private LocalDateTime changedAt;

    /**
     * Constructor que registra un cambio de estado en una inscripción.
     * @param fromState Estado anterior
     * @param toState Estado nuevo
     * @param changedAt Fecha y hora del cambio
     */
    public RegistrationStateChange(RegistrationState fromState, RegistrationState toState, LocalDateTime changedAt) {
        this.id = UUID.randomUUID();
        this.fromState = fromState;
        this.toState = toState;
        this.changedAt = changedAt;
    }
}

