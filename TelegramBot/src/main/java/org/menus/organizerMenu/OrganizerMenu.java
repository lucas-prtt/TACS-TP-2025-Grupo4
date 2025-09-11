package org.menus.organizerMenu;

import org.eventServerClient.ApiClient;
import org.menus.browseMenu.BrowseMenu;
import org.menus.participantMenu.ParticipantMenu;
import org.menus.userMenu.UserMenu;
import org.users.TelegramUser;
import org.menus.MenuState;

public class OrganizerMenu extends MenuState {
    @Override
    public String respondTo(String message) {
        switch (message){
            case "/newEvent":
                return user.setMenuAndRespond(new NewEventMenu(user)); //TODO
            case "/manageEvent":
                return user.setMenuAndRespond(new ManageEventsMenu(user));  //TODO
            default:
                return "Error - opcion invalida\n" + user.getMenu().getQuestion();
        }

    }

    @Override
    public String getQuestion() {
        return """
                Menu de organizador:
                
                /newEvent
                    - Crear evento
                /manageEvent
                    - Modificar evento en base a su UUID
                /start
                    - Volver al menú principal
                
                """;
    }

    public OrganizerMenu(TelegramUser user) {
        super(user);
    }
}
