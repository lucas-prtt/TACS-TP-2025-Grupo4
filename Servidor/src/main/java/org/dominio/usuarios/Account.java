package org.dominio.usuarios;

import org.dominio.events.Registration;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.UUID;

@Getter
@Setter
public class Account {
    UUID uuid;
    String username;
    String salt; //Temporales, hasta que sepamos implementarlo
    String passwordHash;    // Idem

    private List<Registration> registrations = new ArrayList<>();
}
