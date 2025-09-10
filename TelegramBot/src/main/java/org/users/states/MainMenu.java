package org.users.states;

import org.users.TelegramUser;

import java.awt.*;

public class MainMenu extends MenuState {

    public MainMenu(TelegramUser user) {
        super(user);
    }

    @Override
    public String getQuestion() {
        return "Menu principal \n 1. Establecer usuario\n 2. Ver usuario actual\n 3. Registrar usuario";
    }

    @Override
    public String respondTo(String message) {
        switch (message){
            case "1":
                return user.setMenuAndRespond(new SetUserMenu(user));
            case "2":
                return "Usuario actual: " + user.getServerAccountId();
            case "3":
                return user.setMenuAndRespond(new RegisterUserMenu(user));
            default:
                return "Error - opcion invalida\n" + user.getMenu().getQuestion();
        }

    }

}
