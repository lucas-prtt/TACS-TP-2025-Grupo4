package org.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
public class RegistrationDTO {
    UUID eventId;
    UUID accountId;
}
