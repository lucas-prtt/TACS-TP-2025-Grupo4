package org.model.accounts;
import org.model.events.Registration;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
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
    @DBRef(lazy = true)
    private List<Registration> registrations = new ArrayList<>();


    /**
     * Constructor por defecto. Inicializa el id y asigna el rol de usuario por defecto.
     */
    public Account() {
        this.id = UUID.randomUUID();
        this.roles.add(new Role("ROLE_USER"));
    }
}


