package org.DTOs;

import lombok.Getter;
import lombok.Setter;
import org.dominio.events.RegistrationState;

import java.util.UUID;

@Setter
@Getter
public class AccountRegistrationDTO {
    UUID eventId;
    String title;
    String description;
    String state;

    public AccountRegistrationDTO(UUID _eventId, String _title, String _description, String _state) {
        this.eventId = _eventId;
        this.title = _title;
        this.description = _description;
        this.state = _state;
    }
}
