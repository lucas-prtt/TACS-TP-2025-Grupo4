package org.dominio.events;


import lombok.Getter;
import org.dominio.usuarios.Account;

@Getter
public class Registration {
    Event event;
    Account user;

    public Registration(Account user1) {
        user=user1;
    }
}
