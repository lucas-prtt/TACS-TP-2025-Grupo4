package org.menus.participantMenu;

import org.eventServerClient.ApiClient;
import org.eventServerClient.dtos.RegistrationDTO;
import org.eventServerClient.dtos.RegistrationStateDTO;
import org.menus.MenuState;
import org.menus.userMenu.UserMenu;
import org.springframework.web.client.RestClientException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.utils.InlineMenuBuilder;

import java.util.List;
import java.util.UUID;

public class CheckRegistrationMenu extends MenuState {
    RegistrationDTO registrationDTO;
    public CheckRegistrationMenu(TelegramUser user, RegistrationDTO registrationDTO) {
        super(user);
        this.registrationDTO = registrationDTO;
    }

    @Override
    public String respondTo(String message) {
        switch (message){
            case "/back":
                user.setMenu(new ParticipantMenu(user));
                return null;
            case "/cancel":
                try {
                    user.getApiClient().cancelRegistration(registrationDTO.getRegistrationId());
                    user.setMenu(new ParticipantMenu(user));
                    return null;
                }catch (RestClientException e){
                    return e.getMessage() + "\n";
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
            message = InlineMenuBuilder.localizedVerticalMenu(user, getQuestion(), "/back", "/start");
        }
        else{
            message = InlineMenuBuilder.localizedVerticalMenu(user, getQuestion(), "/cancel", "/back", "/start");
        }
        return message;
    }
}
