package org.model.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.DTOs.events.EventDTO;
import org.model.enums.EventState;
import org.model.accounts.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    UUID id;
    String title;
    String description;
    @DBRef(lazy = true)
    Account organizer;
    LocalDateTime startDateTime;
    Integer durationMinutes;
    String location;
    Integer maxParticipants;
    Integer minParticipants; // Puede estar en null para ser opcional
    Integer availableSeats;
    BigDecimal price;
    Category category;
    List<Tag> tags;
    @DBRef(lazy = true)
    List<Registration> participants = new ArrayList<>();
    @DBRef(lazy = true)
    List<Registration> waitList = new ArrayList<>();
    EventState eventState;
    private String image;
    /**
     * Constructor principal de Event. Requiere los campos obligatorios para crear un evento.
     * @param title Título del evento
     * @param description Descripción del evento
     * @param startDateTime Fecha y hora de inicio
     * @param durationMinutes Duración en minutos
     * @param location Ubicación
     * @param maxParticipants Máximo de participantes
     * @param minParticipants Mínimo de participantes (opcional)
     * @param price Precio del evento
     * @param category Categoría del evento (opcional)
     * @param tags Lista de tags (opcional)
     * @param organizer Organizador del evento
     * @param image Url de la imagen de portada del evento (opcional)
     */
    public Event(String title, String description, LocalDateTime startDateTime, Integer durationMinutes, String location, Integer maxParticipants, Integer minParticipants, BigDecimal price, Category category, List<Tag> tags, Account organizer, String image) throws NullPointerException{
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
        this.availableSeats = maxParticipants;
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
        this.image = image;

        // Automaticos
        this.participants = new ArrayList<>();
        this.waitList = new ArrayList<>();
        this.id = UUID.randomUUID();
        this.eventState=EventState.EVENT_OPEN;
    }
    /**
     * Devuelve un nuevo EventBuilder para construir eventos de forma flexible.
     */
    public static EventBuilder Builder(){return new EventBuilder();}

    /**
     * Cierra las inscripciones del evento, cambiando su estado a cerrado.
     */
    public void closeRegistrations() {
        this.eventState=EventState.EVENT_CLOSED;
    }

    /**
     * Indica si las inscripciones al evento están abiertas.
     * @return true si el evento está abierto, false si está cerrado
     */
    public boolean isRegistrationsOpen(){return eventState.equals(EventState.EVENT_OPEN);}


    /**
     * Actualiza los campos del evento según los valores no nulos del DTO recibido.
     * @param dto DTO con los datos a actualizar
     */
    public void patch(EventDTO dto){
        if(dto.getPrice() != null)
            this.price = dto.getPrice();
        if(dto.getCategory() != null)
            this.category = dto.getCategory();
        if(dto.getLocation() != null)
            this.location = dto.getLocation();
        if(dto.getState() != null)
            this.eventState = dto.getState();
        if(dto.getTags() != null)
            this.tags = dto.getTags();
        if(dto.getTitle() != null)
            this.title = dto.getTitle();
        if(dto.getStartDateTime() != null)
            this.startDateTime = dto.getStartDateTime();
        if(dto.getDescription() != null)
            this.description = dto.getDescription();
        if(dto.getDurationMinutes() != null)
            this.durationMinutes = dto.getDurationMinutes();
//        if(dto.getMaxParticipants() != null) {
//            // Recalcular availableSeats respecto a los participantes ya confirmados
//            int currentParticipants = (this.participants == null) ? 0 : this.participants.size();
//            this.maxParticipants = dto.getMaxParticipants();
//            int computedAvailable = this.maxParticipants - currentParticipants;
//            this.availableSeats = Math.max(0, computedAvailable);
//        }
//        if(dto.getMinParticipants() != null)
//            this.minParticipants = dto.getMinParticipants();
        if(dto.getImage() != null)
            this.image = dto.getImage();
    }

}
