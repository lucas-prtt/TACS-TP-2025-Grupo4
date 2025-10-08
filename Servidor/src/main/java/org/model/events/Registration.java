package org.model.events;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

import org.model.enums.RegistrationState;
import org.model.accounts.Account;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "registrations")
@Setter
@Getter
@AllArgsConstructor
public class Registration {
    @Id
    private UUID id;
    @DBRef(lazy = true)
    private Event event;
    @DBRef(lazy = true)
    private Account user;
    private RegistrationState currentState;
    @DBRef(lazy = true)
    private List<RegistrationStateChange> history = new ArrayList<>();
    private LocalDateTime dateTime;

    /**
     * Constructor que inicializa la inscripción con el usuario dado.
     * @param user1 Usuario que se inscribe
     */
    public Registration(Account user1) {
        user=user1;
    }

    /**
     * Constructor por defecto. Inicializa el id y la fecha de inscripción.
     */
    public Registration(){
        this.id = UUID.randomUUID();
        this.dateTime = LocalDateTime.now();
    }

    /**
     * Constructor que inicializa la inscripción con evento, usuario y estado.
     * @param event Evento al que se inscribe
     * @param user Usuario que se inscribe
     * @param state Estado inicial de la inscripción
     */
    public Registration( Event event, Account user, RegistrationState state) {
        this.id = UUID.randomUUID();
        this.dateTime = LocalDateTime.now();
        this.event = event;
        this.user = user;
        this.currentState = state;
    }

    /**
     * Cambia el estado de la inscripción y registra el cambio en el historial.
     * @param newState Nuevo estado de la inscripción
     */
    public void setState(RegistrationState newState) {
        RegistrationStateChange change = new RegistrationStateChange(this, this.currentState, newState, LocalDateTime.now());
        history.add(change);
        this.currentState = newState;
    }

    public String toString(){
        return "(" + id + " - " + event.getTitle() + " - " + user.getUsername() + " - " + currentState + " - " + history.size() + ")";
    }


}