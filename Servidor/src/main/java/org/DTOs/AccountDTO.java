package org.DTOs;

import lombok.Getter;
import lombok.Setter;
import org.dominio.events.Event;
import org.dominio.events.Registration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class AccountDTO {
    UUID uuid;
    String username;
    String salt;
    String passwordHash;

    private List<Registration> registrations = new ArrayList<>();
    private List<Event> waitlists = new ArrayList<>();
}
