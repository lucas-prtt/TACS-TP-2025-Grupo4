package org.menus.participantMenu;

import org.users.TelegramUser;
import org.menus.MenuState;

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
