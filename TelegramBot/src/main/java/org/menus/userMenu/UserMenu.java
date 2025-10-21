package org.menus.userMenu;

import org.menus.MainMenu;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.menus.MenuState;
import org.utils.InlineMenuBuilder;

import java.util.List;
import java.util.Map;

public class UserMenu extends MenuState {
    @Override
    public String respondTo(String message) {
        switch (message){
            case "/oneTimeCode":
                user.setMenu(new OneTimeCodeMenu(user));
                return null;
            case "/loginUser":
                user.setMenu(new LoginUserMenu(user));
                return null;
            case "/registerUser":
                user.setMenu(new RegisterUserMenu(user));
                return null;
            case "/back":
                user.setMenu(new MainMenu(user));
                return null;
            case "/login":
                user.deleteCurrentAccount();
                return null;

            default:
                return user.getLocalizedMessage("wrongOption");
        }
    }

    @Override
    public String getQuestion() {
        return user.getLocalizedMessage("userMenuQuestion");
    }
    @Override
    public SendMessage questionMessage() {
        SendMessage message = InlineMenuBuilder.localizedMenu(user, getQuestion(), List.of("/oneTimeCode"), List.of("/registerUser", "/loginUser"), List.of("/logout"), List.of("/back"));
        return message;
    }

    public UserMenu(TelegramUser user) {
        super(user);
    }
}
