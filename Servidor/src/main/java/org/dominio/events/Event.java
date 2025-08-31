package org.dominio.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dominio.Enums.EventState;
import org.dominio.usuarios.Account;
import org.services.EventService;

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
    LocalDateTime startDateTime;
    Integer durationMinutes;
    String location;
    Integer maxParticipants;
    Integer minParticipants; // Puede estar en null para ser opcional
    BigDecimal price;
    Category category;
    List<Tag> tags;
    List<Registration> participants;
    Queue<Account> waitList;
    EventState eventState;
    // Constructor de Event. Requiere: String title, String description, LocalDateTime startDateTime, Integer durationMinutes, String location, Integer maxParticipants, BigDecimal price
    public Event(String title, String description, LocalDateTime startDateTime, Integer durationMinutes, String location, Integer maxParticipants, Integer minParticipants, BigDecimal price, Category category, List<Tag> tags) throws NullPointerException{
        //Obligatorios
        Objects.requireNonNull(title);
        Objects.requireNonNull(description);
        Objects.requireNonNull(startDateTime);
        Objects.requireNonNull(durationMinutes);
        Objects.requireNonNull(location);
        Objects.requireNonNull(maxParticipants);
        Objects.requireNonNull(price);
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.durationMinutes = durationMinutes;
        this.location = location;
        this.maxParticipants = maxParticipants;
        this.price = price;

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
        this.eventState=EventState.ABIERTO;
    }
    public static EventBuilder Builder(){return new EventBuilder();}

    public void closeRegistrations() {
        this.eventState=EventState.CERRADO;
    }

    public boolean isRegistrationsOpen(){

        if(eventState.equals(EventState.ABIERTO)){
            return true;
        }
        return false;
    }
    public boolean hasAvailableSpots() {
        return participants.size() < maxParticipants;
    }
    public String registerParticipant(){
        //metodo para inscribir participante al evento verificando las condiciones:
        //-hay cupo-si las inscripciones estan abiertas ,etc

        if(isRegistrationsOpen() ){

            if(hasAvailableSpots()){
                //inscribir
            }else {
                //añadir a waitlist
            }
        }
        return null;
    }

}
