package org.DTOs.registrations;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.model.events.Registration;
import org.model.enums.RegistrationState;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDTO {
    private UUID registrationId;
    private UUID eventId;
    private UUID accountId;
    private String title;
    private String description;
    private RegistrationState state;
    private LocalDateTime dateTime;

    public static RegistrationDTO toRegistrationDTO(Registration reg) {
        return new RegistrationDTO(
                reg.getId(),
                reg.getEvent().getId(),
                reg.getUser().getId(),
                reg.getEvent().getTitle(),
                reg.getEvent().getDescription(),
                reg.getCurrentState(),
                reg.getDateTime()
        );
    }
}
