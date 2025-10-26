package org.menus.organizerMenu;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.menus.MenuState;
import org.utils.InlineMenuBuilder;

import java.util.List;
import java.util.Map;

public class OrganizerMenu extends MenuState {
    @Override
    public String respondTo(String message) {
        switch (message){
            case "/newEvent":
                user.setMenu(new NewEventMenu());
                return null;
            case "/manageEvents":
                user.setMenu(new ManageEventSelectionMenu());
                return null;
            default:
                return user.getLocalizedMessage("wrongOption");
        }

    }

    @Override
    public String getQuestion() {
        return user.getLocalizedMessage("organizerMenuQuestion", user.getLocalizedMessage("/newEvent"), user.getLocalizedMessage("/manageEvents"), user.getLocalizedMessage("/start"));
    }

    public OrganizerMenu() {
        super();
    }
    @Override
    public SendMessage questionMessage() {
        SendMessage message = InlineMenuBuilder.localizedVerticalMenu(user, getQuestion(),"/newEvent", "/manageEvents", "/start");
        return message;
    }
}
