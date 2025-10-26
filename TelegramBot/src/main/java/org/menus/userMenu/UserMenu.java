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
                user.setMenu(new OneTimeCodeMenu());
                return null;
            case "/loginUser":
                user.setMenu(new LoginUserMenu());
                return null;
            case "/registerUser":
                user.setMenu(new RegisterUserMenu());
                return null;
            case "/back":
                user.setMenu(new MainMenu());
                return null;
            case "/login":
                user.deleteCurrentAccount();
                return null;
            case "/logout":
                user.deleteCurrentAccount();
                return user.getLocalizedMessage("logoutConfirmed");

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
        SendMessage message = InlineMenuBuilder.localizedMenu(user, getQuestion(), List.of("/oneTimeCode"), List.of("/registerUser", "/loginUser"), user.getServerAccountId() == null ? List.of() : List.of("/logout"), List.of("/back"));
        return message;
    }

    public UserMenu() {
        super();
    }
}
