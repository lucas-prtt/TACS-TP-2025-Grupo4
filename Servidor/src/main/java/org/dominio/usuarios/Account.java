package org.dominio.usuarios;

import lombok.NoArgsConstructor;
import org.dominio.events.Registration;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.UUID;

@Getter
@Setter
public class Account {
    public Account(String username) {
        this.username = username;
        this.id = UUID.randomUUID();
    }

    UUID id;
    String username;
    String salt; //Temporales, hasta que sepamos implementarlo
    String passwordHash;    // Idem

    private List<Registration> registrations = new ArrayList<>();
    public Account(){
        this.id = UUID.randomUUID();
    }
}
