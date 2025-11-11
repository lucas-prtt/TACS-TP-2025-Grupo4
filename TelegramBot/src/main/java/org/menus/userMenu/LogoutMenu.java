package org.menus.userMenu;

import org.menus.MenuState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.utils.InlineMenuBuilder;

import java.util.List;

public class LogoutMenu extends MenuState {
    public LogoutMenu(){
        super();
    }

    @Override
    public String respondTo(String message) {
        switch (message){
            case "/confirm":
                user.deleteCurrentAccount();
                return user.getLocalizedMessage("logoutConfirmed");
            case "/cancel" :
                user.setMenu(new UserMenu());
                return null;
            default:
                return user.getLocalizedMessage("wrongOption");
        }
    }

    @Override
    public String getQuestion() {
        return user.getLocalizedMessage("requestLogoutConfirmation");
    }

    @Override
    public SendMessage questionMessage() {
        return InlineMenuBuilder.localizedMenu(user, getQuestion(), List.of("/cancel", "/confirm"));
    }
}
