package org.menus.browseMenu;

import org.menus.MenuState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class FilterByCategoryMenu extends MenuState {
    public FilterByCategoryMenu() {
        super();
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
