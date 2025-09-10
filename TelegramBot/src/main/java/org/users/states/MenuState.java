package org.users.states;

import org.users.TelegramUser;

public abstract class MenuState {
    abstract public String respondTo(String message);
    abstract public String getQuestion();
    TelegramUser user;
    public MenuState(TelegramUser user){
        this.user = user;
    }
}
