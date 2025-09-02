package org.dominio.events;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.dominio.usuarios.Account;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Registration {
    private UUID id;
    private Event event;
    private Account user;
    private RegistrationState state;
    private LocalDateTime dateTime;

    public Registration(){
        this.id = UUID.randomUUID();
    }

    public Registration( Event event, Account user, RegistrationState state) {
        this.id = UUID.randomUUID();
        this.event = event;
        this.user = user;
        this.state = state;
    }
}