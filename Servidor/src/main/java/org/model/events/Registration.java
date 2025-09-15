package org.model.events;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

import org.DTOs.registrations.RegistrationDTO;
import org.model.enums.RegistrationState;
import org.model.accounts.Account;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Registration {

    public Registration(Account user1) {
        user=user1;
    }
    private UUID id;
    private Event event;
    private Account user;
    private RegistrationState currentState;
    private List<RegistrationStateChange> history = new ArrayList<>();
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
        this.currentState = state;
    }

    public void setState(RegistrationState newState) {
        RegistrationStateChange change = new RegistrationStateChange(this, this.currentState, newState, LocalDateTime.now());
        history.add(change);
        this.currentState = newState;
    }


}