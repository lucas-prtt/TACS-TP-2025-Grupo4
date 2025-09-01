package org.dominio.usuarios;

import lombok.NoArgsConstructor;
import org.dominio.events.Registration;
import org.dominio.events.Event;
import lombok.Getter;
import lombok.Setter;
import java.util.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Account {
    UUID uuid;

    public Account(String username) {
        this.username = username;
    }

    String username;
    String salt; //Temporales, hasta que sepamos implementarlo
    String passwordHash;    // Idem

    private List<Registration> registrations = new ArrayList<>();
    private List<Event> waitlists = new ArrayList<>();
}
