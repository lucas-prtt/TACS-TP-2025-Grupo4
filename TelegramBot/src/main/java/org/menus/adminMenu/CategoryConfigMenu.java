package org.menus.adminMenu;

import org.menus.MenuState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.utils.InlineMenuBuilder;

public class CategoryConfigMenu extends MenuState {
    public CategoryConfigMenu(TelegramUser user) {
        super(user);
    }

    @Override
    public String respondTo(String message) {
        return "NO IMPLEMENTADO";
    }

    @Override
    public String getQuestion() {
        return "NO IMPLEMENTADO";
    }

    @Override
    public SendMessage questionMessage() {
        return InlineMenuBuilder.localizedVerticalMenu(user, "NO IMPLEMENTADO", "/start");
    }
}
