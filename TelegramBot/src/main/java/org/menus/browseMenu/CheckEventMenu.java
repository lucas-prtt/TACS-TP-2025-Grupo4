package org.menus.browseMenu;

import org.eventServerClient.ApiClient;
import org.eventServerClient.dtos.event.EventDTO;
import org.menus.MenuState;
import org.menus.userMenu.UserMenu;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.users.TelegramUser;

import java.util.UUID;

public class CheckEventMenu extends MenuState {
    EventDTO evento;
    public CheckEventMenu(TelegramUser user, EventDTO eventDTO) {
        super(user);
        evento = eventDTO;
    }

    @Override
    public String respondTo(String message) {
        if (message.equals("/register")) {
            if (user.getServerAccountId() == null) {
                return "Primero debe registarse" + user.setMenuAndRespond(new UserMenu(user));
            }

            try {
                String response = ApiClient.postRegistration(evento.getId(), UUID.fromString(user.getServerAccountId()));
                if (response.equalsIgnoreCase("CONFIRMED")) {
                    return "Inscripcion confirmada a la lista de participantes\n\n" + user.setMainMenuAndRespond();
                } else if (response.equalsIgnoreCase("WAITLIST")) {
                    return "Inscripcion confirmada a la Waitlist\n\n" + user.setMainMenuAndRespond();
                } else {
                    return "ERROR DESCONOCIDO\n\n" + user.setMainMenuAndRespond();
                }
            } catch (RestClientResponseException e) {
                return e.getStatusCode().toString() + "\n" + e.getResponseBodyAsString() + "\n\n" + user.setMainMenuAndRespond();
            }
        }
        return "Respuesta invalida \n\n" + getQuestion();
    }

    @Override
    public String getQuestion() {
        return evento.asDetailedString() +
                "\n\n"+
                "/register --> registrarse al evento\n"+
                "/start    --> volver al menu inicial";
    }
}
