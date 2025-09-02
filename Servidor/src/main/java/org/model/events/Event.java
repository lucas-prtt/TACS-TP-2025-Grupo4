package org.model.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.model.enums.EventState;
import org.model.enums.RegistrationState;
import org.model.accounts.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
public class Event {
    UUID id;
    String title;
    String description;
    Account organizer;
    LocalDateTime startDateTime;
    Integer durationMinutes;
    String location;
    Integer maxParticipants;
    Integer minParticipants; // Puede estar en null para ser opcional
    BigDecimal price;
    Category category;
    List<Tag> tags;
    List<Registration> participants;
    Queue<Registration> waitList;
    EventState eventState;
    // Constructor de Event. Requiere: String title, String description, LocalDateTime startDateTime, Integer durationMinutes, String location, Integer maxParticipants, BigDecimal price, Account organizer
    public Event(String title, String description, LocalDateTime startDateTime, Integer durationMinutes, String location, Integer maxParticipants, Integer minParticipants, BigDecimal price, Category category, List<Tag> tags, Account organizer) throws NullPointerException{
        //Obligatorios
        Objects.requireNonNull(title);
        Objects.requireNonNull(description);
        Objects.requireNonNull(startDateTime);
        Objects.requireNonNull(durationMinutes);
        Objects.requireNonNull(location);
        Objects.requireNonNull(maxParticipants);
        Objects.requireNonNull(price);
        Objects.requireNonNull(organizer);
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.durationMinutes = durationMinutes;
        this.location = location;
        this.maxParticipants = maxParticipants;
        this.price = price;
        this.organizer = organizer;

        //Opcionales
        this.category = category;
        this.minParticipants = minParticipants;
        if(tags != null) {
            this.tags = tags;
        }
        else {
            this.tags = new ArrayList<>();
        }

        // Automaticos
        this.participants = new ArrayList<>();
        this.waitList = new ArrayDeque<>();
        this.id = UUID.randomUUID();
        this.eventState=EventState.EVENT_OPEN;
    }
    public static EventBuilder Builder(){return new EventBuilder();}

    public void closeRegistrations() {
        this.eventState=EventState.EVENT_CLOSED;
    }

    public boolean isRegistrationsOpen(){return eventState.equals(EventState.EVENT_OPEN);}

    public boolean hasAvailableSpots() {
        return participants.size() < maxParticipants;
    }

    public String registerParticipant(Registration registration) {
        //metodo para inscribir participante al evento verificando las condiciones:
        //-hay cupo-si las inscripciones estan abiertas ,etc

        if (isRegistrationsOpen()) {
            if (hasAvailableSpots()) {
                //inscribir
                registration.setState(RegistrationState.CONFIRMED);
                participants.add(registration);
                registration.getUser().getRegistrations().add(registration);
            } else {
                //añadir a waitlist
                registration.setState(RegistrationState.WAITLIST);
                waitList.add(registration);
                registration.getUser().getRegistrations().add(registration);
            }
            return registration.getCurrentState().toString();
        } else {
            return EventState.EVENT_CLOSED.toString();
        }
    }

    public void promoteFromWaitlist() {
        if (participants.size() < maxParticipants && !waitList.isEmpty()) {
            Registration next = waitList.poll();  // saca el primero
            next.setState(RegistrationState.CONFIRMED);
            participants.add(next);
        }
    }

}
