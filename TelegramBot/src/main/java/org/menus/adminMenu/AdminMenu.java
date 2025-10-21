package org.menus.adminMenu;

import org.menus.MenuState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.utils.InlineMenuBuilder;

public class AdminMenu extends MenuState {
    public AdminMenu(TelegramUser user) {
        super(user);
    }

    @Override
    public String respondTo(String message) {
        return "NOT IMPLEMENTED";
    }

    @Override
    public String getQuestion() {
        return "NOT IMPLEMENTED";
    }

    @Override
    public SendMessage questionMessage() {
        return InlineMenuBuilder.localizedVerticalMenu(user, "NOT IMPLEMENTED");
    }
}
