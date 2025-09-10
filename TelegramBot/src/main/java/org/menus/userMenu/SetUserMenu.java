package org.menus.userMenu;

import org.users.TelegramUser;
import org.menus.MenuState;

public class SetUserMenu extends MenuState {

    public SetUserMenu(TelegramUser user) {
        super(user);
    }

    @Override
    public String respondTo(String message) {
        //TODO: Chequear que exista, setear nombre, setear uuid
        user.setServerAccountUsername(message);
        return user.setMainMenuAndRespond();
    }

    @Override
    public String getQuestion() {
        return "Ingrese el userId";
    }
}
