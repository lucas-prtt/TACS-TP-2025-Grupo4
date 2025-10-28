package org.eventServerClient.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.users.TelegramUser;

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
    public String toShortString(TelegramUser user){
        return user.getLocalizedMessage("registrationAsShortString", user.getLocalizedMessage(state.toString()), title);
    }
    public String toDetailedString(TelegramUser user){
        return user.getLocalizedMessage("registrationAsDetailedString", user.getLocalizedMessage(state.toString()), title, (description.length()<1000 ? description : description.substring(0, 996).concat("...")), user.localizeDate(dateTime));
    }

}
