package org.dominio.events;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.dominio.usuarios.Account;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Registration {
    Event event;
    Account user;

    public Registration(Account user1) {
        user=user1;
    }
    RegistrationState state;
}