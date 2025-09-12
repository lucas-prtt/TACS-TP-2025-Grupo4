package org.menus.organizerMenu;

import org.eventServerClient.dtos.event.EventDTO;
import org.menus.MenuState;
import org.users.TelegramUser;

public class ManageEventMenu extends MenuState {
    EventDTO event;
    public ManageEventMenu(TelegramUser user, EventDTO item) {
        super(user);
        this.event = item;
    }

    @Override
    public String respondTo(String message) {
        return "NO IMPLEMENTADO";
    }

    @Override
    public String getQuestion() {
        return "NO IMPLEMENTADO \n /start para volver";
    }
}
