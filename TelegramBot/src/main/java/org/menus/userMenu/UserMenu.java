package org.menus.userMenu;

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

            default:
                return "Error - opcion invalida\n\n";
        }
    }

    @Override
    public String getQuestion() {
        return """
               \s
                 Menu de Usuario\s
                \s
                - Use one-time-code: Login con un código unico disponible desde la web
               \s
                - Login: Login con username y contraseña
               \s
                - Register: Registrar usuario nuevo con username y contraseña
               \s
               Aclaración: Se recomienda loguearse mediante one-time-code por motivos de seguridad
               """;
    }
    @Override
    public SendMessage questionMessage() {
        SendMessage message = InlineMenuBuilder.menu(getQuestion(), Map.of("Use one-time-code","/oneTimeCode" ), Map.of("Register", "/registerUser", "Login", "/loginUser"));
        return message;
    }

    public UserMenu(TelegramUser user) {
        super(user);
    }
}
