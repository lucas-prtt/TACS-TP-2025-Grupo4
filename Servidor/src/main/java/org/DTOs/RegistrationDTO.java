package org.DTOs;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class RegistrationDTO {
    UUID eventId;
    UUID accountId;
}
