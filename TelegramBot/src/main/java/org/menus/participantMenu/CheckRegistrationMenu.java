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
                return "Opcion invalida\n";
        }
    }

    @Override
    public String getQuestion() {
        return registrationDTO.toDetailedString() +
                (registrationDTO.getState()==RegistrationStateDTO.CANCELED? "" :  "\n/cancel --> Cancelar registro") +
                "\n/back" +
                "\n/start";
    }
    @Override
    public SendMessage questionMessage() {
        SendMessage message;
        if (registrationDTO.getState() == RegistrationStateDTO.CANCELED){
            message = InlineMenuBuilder.menu(getQuestion(), List.of("/back", "/start"));
        }
        else{
            message = InlineMenuBuilder.menu(getQuestion(), List.of("/cancel", "/back", "/start"));
        }
        return message;
    }
}
