package org.menus.browseMenu;

import org.apache.http.client.HttpResponseException;
import org.eventServerClient.ApiClient;
import org.eventServerClient.dtos.event.EventDTO;
import org.menus.MenuState;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.users.TelegramUser;

import java.util.UUID;

public class LookUpEventByUUIDMenu extends MenuState {
    @Override
    public String respondTo(String message) {
        try {
            EventDTO eventDTO = user.getApiClient().getEvent(UUID.fromString(message));
            return "Evento encontrado \n\n" + user.setMenuAndRespond(new CheckEventMenu(user, eventDTO));
        }catch (RestClientResponseException e){
            return e.getStatusCode() + "\n\n" + e.getMessage() + "\n\n" + getQuestion();
        }
    }

    @Override
    public String getQuestion() {
        return "Ingrese la ID del evento\n(o ingrese /start para volver al menu principal)";
    }

    public LookUpEventByUUIDMenu(TelegramUser user) {
        super(user);
    }
}
