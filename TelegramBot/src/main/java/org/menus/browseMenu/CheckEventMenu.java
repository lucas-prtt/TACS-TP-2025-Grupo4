package org.menus.browseMenu;

import org.eventServerClient.ApiClient;
import org.eventServerClient.dtos.event.EventDTO;
import org.menus.MainMenu;
import org.menus.MenuState;
import org.menus.userMenu.UserMenu;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.utils.InlineMenuBuilder;

import java.util.List;
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
                user.setMenu(new UserMenu(user));
                return "Primero debe registarse" ;
            }

            try {
                String response = user.getApiClient().postRegistration(evento.getId());
                if (response.equalsIgnoreCase("CONFIRMED")) {
                    user.setMenu(new MainMenu(user));
                    return "Inscripcion confirmada a la lista de participantes\n\n";
                } else if (response.equalsIgnoreCase("WAITLIST")) {
                    user.setMenu(new MainMenu(user));
                    return "Inscripcion confirmada a la Waitlist\n\n";
                } else {
                    user.setMenu(new MainMenu(user));
                    return "ERROR DESCONOCIDO\n\n";
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
    @Override
    public SendMessage questionMessage() {
        SendMessage message = InlineMenuBuilder.menu(getQuestion(), List.of("/register", "/start"));
        return message;
    }

}
