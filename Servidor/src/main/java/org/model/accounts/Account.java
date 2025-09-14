package org.model.accounts;

import org.model.events.Registration;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class Account {
    private UUID id;
    private String username;
    private String password; // Guardar encriptado con BCrypt
    private Set<Role> roles = new HashSet<>();
    private List<Registration> registrations = new ArrayList<>();

    public Account(String username, String password) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.password = password;
        this.roles.add(new Role("ROLE_USER")); // por defecto todos arrancan como usuario común
    }

    public Account() {
        this.id = UUID.randomUUID();
        this.roles.add(new Role("ROLE_USER"));
    }
}


