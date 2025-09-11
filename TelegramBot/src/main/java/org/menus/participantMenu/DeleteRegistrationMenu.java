package org.menus.participantMenu;

import org.menus.MenuState;
import org.users.TelegramUser;

public class DeleteRegistrationMenu extends MenuState {
    @Override
    public String respondTo(String message) {
        return "NO IMPLEMENTADO";
    }

    @Override
    public String getQuestion() {
        return "NO IMPLEMENTADO\nUtilice /start para volver al menu principal";
    }

    public DeleteRegistrationMenu(TelegramUser user) {
        super(user);
    }
}
