package org.dominio.events;


import lombok.NoArgsConstructor;
import org.dominio.usuarios.Account;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class Registration {
    Event event;
    Account user;

    public Registration(Account user1) {
        user=user1;
    }
}