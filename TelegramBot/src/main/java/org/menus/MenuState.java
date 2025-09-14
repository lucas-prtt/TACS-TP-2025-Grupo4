package org.menus;

import org.users.TelegramUser;

public abstract class MenuState {
    abstract public String respondTo(String message);
    abstract public String getQuestion();
    protected TelegramUser user;
    public MenuState(TelegramUser user){
        this.user = user;
    }
}
