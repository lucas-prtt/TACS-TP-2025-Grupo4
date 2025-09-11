package org.menus.browseMenu;

import org.eventServerClient.dtos.event.EventDTO;
import org.menus.MenuState;
import org.users.TelegramUser;

public class CheckEventMenu extends MenuState {
    EventDTO evento;
    public CheckEventMenu(TelegramUser user, EventDTO eventDTO) {
        super(user);
        evento = eventDTO;
    }

    @Override
    public String respondTo(String message) {
        return "NO IMPLEMENTADO";
    }

    @Override
    public String getQuestion() {
        return "NO IMPLEMENTADO, evento = " + evento.getTitle();
    }
}
