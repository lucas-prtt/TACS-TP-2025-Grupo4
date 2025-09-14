package org.eventServerClient.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationDTO {
    private UUID registrationId;
    private UUID eventId;
    private UUID accountId;
    private String title;
    private String description;
    private RegistrationStateDTO state;
    private LocalDateTime dateTime;
    public String toShortString(){
        return "Estado: " + state.toString().toLowerCase() + "\n  Evento: " + title + "\n  Id: " + registrationId + "\n\n";
    }
    public String toDetailedString(){
        return "Estado: " + state.toString().toLowerCase() + "\n  Evento: " + title + "\n Descipcion: " + (description.length()<1000 ? description : description.substring(0, 996).concat("...")) +
                "\n  Id: " + registrationId + "\n" + "Fecha de inicio" + dateTime;
    }
    @Override
    public String toString(){
        return toDetailedString();
    }

}
