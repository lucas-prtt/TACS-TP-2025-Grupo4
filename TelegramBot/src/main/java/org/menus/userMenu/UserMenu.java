package org.menus.userMenu;

import org.users.TelegramUser;
import org.menus.MenuState;

public class UserMenu extends MenuState {
    @Override
    public String respondTo(String message) {
        switch (message){
            case "/oneTimeCode":
                return user.setMenuAndRespond(new OneTimeCodeMenu(user));
            case "/loginUser":
                return user.setMenuAndRespond(new LoginUserMenu(user)) ;
            case "/registerUser":
                return user.setMenuAndRespond(new RegisterUserMenu(user));

            default:
                return "Error - opcion invalida\n\n" + user.getMenu().getQuestion();
        }
    }

    @Override
    public String getQuestion() {
        return "Menu Usuario \n " +
                "/oneTimeCode: Login con one time code\n " +
                "/loginUser: Login con username y contraseña\n " +
                "/registerUser: Registrar usuario nuevo con username y contraseña";
    }

    public UserMenu(TelegramUser user) {
        super(user);
    }
}
