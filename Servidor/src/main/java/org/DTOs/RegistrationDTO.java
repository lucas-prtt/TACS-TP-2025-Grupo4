package org.DTOs;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.dominio.events.RegistrationState;

import java.util.UUID;
import org.dominio.events.Registration;
import org.dominio.events.RegistrationState;

@Setter
@Getter
@AllArgsConstructor
public class RegistrationDTO {
    private UUID registrationId;
    private UUID eventId;
    private UUID accountId;
    private RegistrationState state;
    private LocalDateTime dateTime;

}
