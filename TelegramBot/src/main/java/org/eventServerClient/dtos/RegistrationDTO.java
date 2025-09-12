package org.eventServerClient.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationDTO {
    private UUID registrationId;
    private UUID eventId;
    private UUID accountId;
    private String title;
    private String description;
    private RegistrationStateDTO state;
    private LocalDateTime dateTime;
}
