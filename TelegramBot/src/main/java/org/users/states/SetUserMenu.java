package org.users.states;

import org.users.TelegramUser;

public class SetUserMenu extends MenuState{

    public SetUserMenu(TelegramUser user) {
        super(user);
    }

    @Override
    public String respondTo(String message) {
        //TODO: Chequear que exista?
        user.setServerAccountId(message);
        return user.setMainMenuAndRespond();
    }

    @Override
    public String getQuestion() {
        return "Ingrese el userId";
    }
}
