package org.dominio.events;

import org.dominio.usuarios.Account;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Registration {
    Event event;
    Account user;
    RegistrationState state;
}