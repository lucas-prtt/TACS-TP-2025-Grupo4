package org.model.accounts;

import lombok.NoArgsConstructor;
import org.model.events.Registration;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "accounts")
@Getter
@Setter
public class Account {
    @Id
    private UUID id;
    private String username;
    private String password; // Guardar encriptado con BCrypt
    private Set<Role> roles = new HashSet<>();
    private List<Registration> registrations = new ArrayList<>();


    public Account() {
        this.id = UUID.randomUUID();
        this.roles.add(new Role("ROLE_USER"));
    }
}


