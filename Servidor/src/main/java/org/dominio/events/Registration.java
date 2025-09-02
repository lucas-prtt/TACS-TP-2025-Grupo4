package org.dominio.events;

import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
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
    private UUID id;
    private Event event;
    private Account user;
    private RegistrationState state;
    private LocalDateTime dateTime;

    public Registration(){
        this.id = UUID.randomUUID();
        this.dateTime = LocalDateTime.now();
    }

    public Registration( Event event, Account user, RegistrationState state) {
        this.id = UUID.randomUUID();
        this.dateTime = LocalDateTime.now();
        this.event = event;
        this.user = user;
        this.state = state;
    }
}