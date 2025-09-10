package org.menus;

import org.users.TelegramUser;
import org.menus.organizerMenu.OrganizerMenu;
import org.menus.participantMenu.ParticipantMenu;
import org.menus.userMenu.UserMenu;

public class MainMenu extends MenuState {

    public MainMenu(TelegramUser user) {
        super(user);
    }

    @Override
    public String getQuestion() {
        return "Menu principal: \n " +
                "/userNenu: Menu de gestion de usuario\n " +
                "/organizerMenu. Menu de gestion de eventos organizados\n " +
                "/participantMenu. Menu de gestion de eventos a los que participa\n";
    }

    @Override
    public String respondTo(String message) {
        switch (message){
            case "/userNenu":
                return user.setMenuAndRespond(new UserMenu(user));
            case "/organizerMenu":
                return user.setMenuAndRespond(new OrganizerMenu(user));
            case "/participantMenu":
                return user.setMenuAndRespond(new ParticipantMenu(user));
            default:
                return "Error - opcion invalida\n" + user.getMenu().getQuestion();
        }

    }

}
