package org.model.events;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.model.enums.RegistrationState;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "registrationStateChanges")
@Getter
@Setter
public class RegistrationStateChange {
    @Id
    private UUID id;
    private Registration registration;
    private RegistrationState fromState;
    private RegistrationState toState;
    private LocalDateTime changedAt;

    public RegistrationStateChange(Registration registration, RegistrationState fromState, RegistrationState toState, LocalDateTime changedAt) {
        this.id = UUID.randomUUID();
        this.registration = registration;
        this.fromState = fromState;
        this.toState = toState;
        this.changedAt = changedAt;
    }
}

