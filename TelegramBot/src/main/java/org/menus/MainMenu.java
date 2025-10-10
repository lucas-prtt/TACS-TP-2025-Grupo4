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
import java.util.Objects;

public class MainMenu extends MenuState {

    public MainMenu(TelegramUser user) {
        super(user);
    }

    @Override
    public String getQuestion() {
        return user.getLocalizedMessage("mainMenuQuestion");
    }

    @Override
    public SendMessage questionMessage() {
        SendMessage message;
        if(user.getServerAccountUsername() != null){
            message = InlineMenuBuilder.localizedVerticalMenu(user, getQuestion(), "/userMenu", "/organizerMenu", "/participantMenu", "/browseMenu", "/languageMenu");
        }else {
            message = InlineMenuBuilder.localizedVerticalMenu(user, getQuestion(), "/userMenu", "/languageMenu");
        }
        return message;
    }


    @Override
    public String respondTo(String message) {
        if(user.getServerAccountUsername() == null &&
                        (Objects.equals(message, "/organizerMenu") ||
                        Objects.equals(message, "/participantMenu") ||
                        Objects.equals(message, "/browseMenu")
                        )
        )
            return "Inicia sesion antes de entrar a estos menus";

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
            case "/languageMenu":
                user.setMenu(new LanguageMenu(user));
                return null;
            default:
                return "Error - opcion invalida\n";
        }

    }

}
