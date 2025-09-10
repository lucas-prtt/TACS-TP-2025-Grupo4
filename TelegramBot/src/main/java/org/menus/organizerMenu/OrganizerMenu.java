package org.menus.organizerMenu;

import org.users.TelegramUser;
import org.menus.MenuState;

public class OrganizerMenu extends MenuState {
    @Override
    public String respondTo(String message) {
        return "";
    }

    @Override
    public String getQuestion() {
        return "";
    }

    public OrganizerMenu(TelegramUser user) {
        super(user);
    }
}
