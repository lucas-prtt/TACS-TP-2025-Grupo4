package org.menus.organizerMenu;

import org.users.TelegramUser;
import org.menus.MenuState;

public class OrganizerMenu extends MenuState {
    @Override
    public String respondTo(String message) {
        switch (message){
            case "/newEvent":
                return user.setMenuAndRespond(new NewEventMenu(user));
            case "/manageEvents":
                return user.setMenuAndRespond(new ManageEventSelectionMenu(user));
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
                /manageEvents
                    - Modificar evento en base a su UUID
                /start
                    - Volver al menú principal
                
                """;
    }

    public OrganizerMenu(TelegramUser user) {
        super(user);
    }
}
