package org.menus.userMenu;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.menus.MenuState;
import org.utils.InlineMenuBuilder;

import java.util.List;

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

            default:
                return "Error - opcion invalida\n\n";
        }
    }

    @Override
    public String getQuestion() {
        return "Menu Usuario \n " +
                "/oneTimeCode: Login con one time code\n " +
                "/loginUser: Login con username y contraseña\n " +
                "/registerUser: Registrar usuario nuevo con username y contraseña";
    }
    @Override
    public SendMessage questionMessage() {
        SendMessage message = InlineMenuBuilder.menu(getQuestion(), List.of("/oneTimeCode"), List.of("/registerUser", "/loginUser"));
        return message;
    }

    public UserMenu(TelegramUser user) {
        super(user);
    }
}
