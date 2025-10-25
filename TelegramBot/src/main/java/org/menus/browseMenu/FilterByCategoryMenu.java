package org.menus.browseMenu;

import org.menus.MenuState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;

import java.awt.*;

public class FilterByCategoryMenu extends MenuState {
    public FilterByCategoryMenu(TelegramUser user) {
        super(user);
    }

    @Override
    public String respondTo(String message) {
        return "";
    }

    @Override
    public String getQuestion() {
        return "";
    }

    @Override
    public SendMessage questionMessage() {
        return null;
    }
}
