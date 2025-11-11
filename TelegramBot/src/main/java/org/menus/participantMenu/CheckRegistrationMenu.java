package org.menus.participantMenu;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eventServerClient.dtos.RegistrationDTO;
import org.eventServerClient.dtos.RegistrationStateDTO;
import org.eventServerClient.dtos.event.EventDTO;
import org.menus.MenuState;
import org.menus.browseMenu.CheckEventMenu;
import org.springframework.web.client.HttpClientErrorException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.utils.ErrorHandler;
import org.utils.InlineMenuBuilder;

@Getter
@Setter
@NoArgsConstructor
public class CheckRegistrationMenu extends MenuState {
    RegistrationDTO registrationDTO;
    public CheckRegistrationMenu(RegistrationDTO registrationDTO) {
        super();
        this.registrationDTO = registrationDTO;
    }

    @Override
    public String respondTo(String message) {
        switch (message){
            case "/checkEvent":
                EventDTO eventDTO;
                try {
                    eventDTO = user.getApiClient().getEvent(registrationDTO.getEventId());
                }catch (HttpClientErrorException e){
                    return ErrorHandler.getErrorMessage(e, user);
                }
                try {
                    user.setMenu(new CheckEventMenu(eventDTO, this));
                    return null;
                }catch (HttpClientErrorException e){
                    return ErrorHandler.getErrorMessage(e, user);
                }
            case "/back":
                user.setMenu(new ParticipantMenu());
                return null;
            case "/cancelRegistration":
                try {
                    user.getApiClient().cancelRegistration(registrationDTO.getRegistrationId());
                    user.setMenu(new ParticipantMenu());
                    return null;
                }catch (HttpClientErrorException e){
                    return ErrorHandler.getErrorMessage(e, user);
                }
            default:
                return user.getLocalizedMessage("wrongOption");
        }
    }

    @Override
    public String getQuestion() {
        return registrationDTO.toDetailedString(user);
    }
    @Override
    public SendMessage questionMessage() {
        SendMessage message;
        if (registrationDTO.getState() == RegistrationStateDTO.CANCELED){
            message = InlineMenuBuilder.localizedVerticalMenu(user, getQuestion(), "/checkEvent","/back", "/start");
        }
        else{
            message = InlineMenuBuilder.localizedVerticalMenu(user, getQuestion(), "/checkEvent","/cancelRegistration", "/back", "/start");
        }
        return message;
    }
}
