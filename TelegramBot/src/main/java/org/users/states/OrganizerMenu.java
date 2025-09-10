package org.users.states;

import org.users.TelegramUser;

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
