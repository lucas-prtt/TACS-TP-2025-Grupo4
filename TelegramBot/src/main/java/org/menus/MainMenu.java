package org.menus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.menus.adminMenu.AdminMenu;
import org.menus.browseMenu.BrowseMenu;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.menus.organizerMenu.OrganizerMenu;
import org.menus.participantMenu.ParticipantMenu;
import org.menus.userMenu.UserMenu;
import org.utils.InlineMenuBuilder;

import java.util.Objects;

public class MainMenu extends MenuState {

    public MainMenu() {
        super();
    }
    @JsonIgnore
    @Override
    public String getQuestion() {
        if(user.getServerAccountUsername() == null)
            return user.getLocalizedMessage("mainMenuQuestionLoggedOut", user.getLocalizedMessage("/userMenu"), user.getLocalizedMessage("/languageMenu"));
        else if(!user.isAdmin())
            return user.getLocalizedMessage("mainMenuQuestionLoggedIn", user.getLocalizedMessage("/userMenu"), user.getLocalizedMessage("/organizerMenu"), user.getLocalizedMessage("/participantMenu"), user.getLocalizedMessage("/browseMenu"), user.getLocalizedMessage("/languageMenu"));
        else
            return user.getLocalizedMessage("mainMenuQuestionAdmin", user.getLocalizedMessage("/userMenu"), user.getLocalizedMessage("/organizerMenu"), user.getLocalizedMessage("/participantMenu"), user.getLocalizedMessage("/browseMenu"), user.getLocalizedMessage("/adminMenu"), user.getLocalizedMessage("/languageMenu"));
    }

    @Override
    public SendMessage questionMessage() {
        SendMessage message;
        if(user.isAdmin()){
            message = InlineMenuBuilder.localizedVerticalMenu(user, getQuestion(), "/userMenu", "/organizerMenu", "/participantMenu", "/browseMenu", "/adminMenu" ,"/languageMenu");
        }else if(user.isUser()){
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
            return user.getLocalizedMessage("mustLoginBeforeMenu");
        if(!user.isAdmin() && Objects.equals(message, "/adminMenu"))
            return user.getLocalizedMessage("mustBeAdmin");
        switch (message){
            case "/userMenu":
                user.setMenu(new UserMenu());
                return null;
            case "/organizerMenu":
                user.setMenu(new OrganizerMenu());
                return null;
            case "/participantMenu":
                user.setMenu(new ParticipantMenu());
                return null;
            case "/browseMenu":
                user.setMenu(new BrowseMenu());
                return null;
            case "/languageMenu":
                user.setMenu(new LanguageMenu());
                return null;
            case "/adminMenu":
                user.setMenu(new AdminMenu());
                return null;
            default:
                return user.getLocalizedMessage("wrongOption");
        }

    }

}
