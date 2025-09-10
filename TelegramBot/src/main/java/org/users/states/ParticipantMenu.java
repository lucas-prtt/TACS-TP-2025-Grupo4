package org.users.states;

import org.users.TelegramUser;

public class ParticipantMenu extends MenuState {
    @Override
    public String respondTo(String message) {
        return "";
    }

    @Override
    public String getQuestion() {
        return "";
    }

    public ParticipantMenu(TelegramUser user) {
        super(user);
    }
}
