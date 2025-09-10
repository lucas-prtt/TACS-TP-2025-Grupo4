package org.menus.browseMenu;

import org.menus.MenuState;
import org.users.TelegramUser;

public class LookUpEventByUUIDMenu extends MenuState {
    @Override
    public String respondTo(String message) {
        return "NO IMPLEMENTADO";
    }

    @Override
    public String getQuestion() {
        return "NO IMPLEMENTADO\nUtilice /start para volver al menu principal";
    }

    public LookUpEventByUUIDMenu(TelegramUser user) {
        super(user);
    }
}
