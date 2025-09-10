package org.menus.userMenu;

import org.users.TelegramUser;
import org.menus.MenuState;

public class UserMenu extends MenuState {
    @Override
    public String respondTo(String message) {
        switch (message){
            case "1":
                return user.setMenuAndRespond(new SetUserMenu(user));
            case "2":
                return "Usuario actual: " + user.getServerAccountId() + "\n" + user.setMainMenuAndRespond();
            case "3":
                return user.setMenuAndRespond(new RegisterUserMenu(user));
            default:
                return "Error - opcion invalida\n" + user.getMenu().getQuestion();
        }
    }

    @Override
    public String getQuestion() {
        return "Menu Usuario \n " +
                "1. Establecer usuario\n " +
                "2. Ver usuario actual\n " +
                "3. Registrar usuario";
    }

    public UserMenu(TelegramUser user) {
        super(user);
    }
}
