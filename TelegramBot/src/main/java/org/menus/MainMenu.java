package org.menus;

import org.menus.browseMenu.BrowseMenu;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.users.TelegramUser;
import org.menus.organizerMenu.OrganizerMenu;
import org.menus.participantMenu.ParticipantMenu;
import org.menus.userMenu.UserMenu;
import org.utils.InlineMenuBuilder;

import java.util.List;
import java.util.Map;

public class MainMenu extends MenuState {

    public MainMenu(TelegramUser user) {
        super(user);
    }

    @Override
    public String getQuestion() {
        return "Menu principal: \n " +
                "/userMenu: Menu de gestion de usuario\n " +
                "/organizerMenu. Menu de gestion de eventos organizados\n " +
                "/participantMenu. Menu de gestion de eventos a los que participa\n" +
                "/browseMenu. Menu para buscar nuevos eventos a los que participar";
    }

    @Override
    public SendMessage questionMessage() {
        SendMessage message = InlineMenuBuilder.menu(getQuestion(), List.of("/userMenu"), List.of( "/organizerMenu"), List.of( "/participantMenu"), List.of( "/browseMenu"));
        return message;
    }


    @Override
    public String respondTo(String message) {
        switch (message){
            case "/userMenu":
                user.setMenu(new UserMenu(user));
                return null;
            case "/organizerMenu":
                user.setMenu(new OrganizerMenu(user));
                return null;
            case "/participantMenu":
                user.setMenu(new ParticipantMenu(user));
                return null;
            case "/browseMenu":
                user.setMenu(new BrowseMenu(user));
                return null;
            default:
                return "Error - opcion invalida\n";
        }

    }

}
