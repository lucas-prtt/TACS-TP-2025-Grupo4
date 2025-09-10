package org.menus.organizerMenu;

import org.menus.MenuState;
import org.users.TelegramUser;

public class ManageEventsMenu extends MenuState {
    @Override
    public String respondTo(String message) {
        return "NO IMPLEMENTADO";
    }

    @Override
    public String getQuestion() {
        return "NO IMPLEMENTADO\nUtilice /start para volver al menu principal";
    }

    public ManageEventsMenu(TelegramUser user) {
        super(user);
    }
}
