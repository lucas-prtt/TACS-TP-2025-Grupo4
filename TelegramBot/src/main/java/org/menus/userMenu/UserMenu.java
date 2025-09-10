package org.menus.userMenu;

import org.users.TelegramUser;
import org.menus.MenuState;

public class UserMenu extends MenuState {
    @Override
    public String respondTo(String message) {
        switch (message){
            case "/setUser":
                return user.setMenuAndRespond(new SetUserMenu(user));
            case "/getUser":
                return "Usuario actual: " + user.getServerAccountId() + "\n" + user.setMainMenuAndRespond();
            case "/registerUser":
                return user.setMenuAndRespond(new RegisterUserMenu(user));
            default:
                return "Error - opcion invalida\n" + user.getMenu().getQuestion();
        }
    }

    @Override
    public String getQuestion() {
        return "Menu Usuario \n " +
                "/setUser: Establecer usuario\n " +
                "/getUser: Ver usuario actual\n " +
                "/registerUser: Registrar usuario";
    }

    public UserMenu(TelegramUser user) {
        super(user);
    }
}
