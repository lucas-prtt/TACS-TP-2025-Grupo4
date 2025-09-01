package org.dominio.usuarios;

import org.dominio.events.Registration;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.UUID;

@Getter
@Setter
public class Account {
    UUID id;
    String username;
    String salt; //Temporales, hasta que sepamos implementarlo
    String passwordHash;    // Idem

    private List<Registration> registrations = new ArrayList<>();
    private List<Registration> waitlists = new ArrayList<>();

    public Account(){
        this.id = UUID.randomUUID();
    }

    public void promoteFromWaitlistOfAccount(Registration reg){
        this.registrations.remove(reg);
        this.waitlists.add(reg);
    }

}
