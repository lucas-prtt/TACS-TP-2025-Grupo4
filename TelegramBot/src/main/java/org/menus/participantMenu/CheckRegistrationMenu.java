package org.menus.participantMenu;

import org.eventServerClient.ApiClient;
import org.eventServerClient.dtos.RegistrationDTO;
import org.eventServerClient.dtos.RegistrationStateDTO;
import org.menus.MenuState;
import org.springframework.web.client.RestClientException;
import org.users.TelegramUser;

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
                return user.setMenuAndRespond(new ParticipantMenu(user));
            case "/cancel":
                try {
                    user.getApiClient().cancelRegistration(UUID.fromString(user.getServerAccountId()), registrationDTO.getRegistrationId());
                    return user.setMenuAndRespond(new ParticipantMenu(user));
                }catch (RestClientException e){
                    return e.getMessage() + "\n" + getQuestion();
                }
            default:
                return "Opcion invalida\n" + getQuestion();
        }
    }

    @Override
    public String getQuestion() {
        return registrationDTO.toDetailedString() +
                (registrationDTO.getState()==RegistrationStateDTO.CANCELED? "" :  "\n/cancel --> Cancelar registro") +
                "\n/back" +
                "\n/start";
    }
}
