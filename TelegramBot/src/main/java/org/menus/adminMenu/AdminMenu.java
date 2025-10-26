package org.menus.adminMenu;

import org.menus.MenuState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.utils.InlineMenuBuilder;

public class AdminMenu extends MenuState {
    public AdminMenu() {
        super();
    }

    @Override
    public String respondTo(String message) {
        switch (message){
            case "/stats":
                user.setMenu(new StatsMenu());
                break;
            case "/categoryConfig":
                user.setMenu(new CategoryConfigMenu());
                break;
            default:
                return user.getLocalizedMessage("wrongOption");
        };
        return null;
    }

    @Override
    public String getQuestion() {
        return user.getLocalizedMessage("adminMenuQuestion", user.getLocalizedMessage("/stats"), user.getLocalizedMessage("/categoryConfig"));
    }

    @Override
    public SendMessage questionMessage() {
        return InlineMenuBuilder.localizedVerticalMenu(user, getQuestion(), "/stats", "/categoryConfig", "/start");
    }
}
