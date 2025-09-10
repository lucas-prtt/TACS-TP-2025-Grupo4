package org.menus.userMenu;

import org.users.TelegramUser;
import org.menus.MenuState;

public class RegisterUserMenu extends MenuState {
    @Override
    public String respondTo(String message) {
        //TODO: POST a servidor
        user.setServerAccountId(message);
        return "Cuenta creada y establecida como la actual\n" + user.setMainMenuAndRespond();
    }

    @Override
    public String getQuestion() {
        return "Ingrese el ID del usuario a crear";
    }

    public RegisterUserMenu(TelegramUser user) {
        super(user);
    }
}
