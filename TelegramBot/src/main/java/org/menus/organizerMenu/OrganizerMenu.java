package org.menus.organizerMenu;

import org.users.TelegramUser;
import org.menus.MenuState;

public class OrganizerMenu extends MenuState {
    @Override
    public String respondTo(String message) {
        return "";
    }

    @Override
    public String getQuestion() {
        return """
                Menu de organizador:
                
                /newEvent
                    - Crear evento
                /getCreatedEvents
                    - Ver eventos creados
                /manageEvent
                    - Modificar evento en base a su UUID
                
                """;
    }

    public OrganizerMenu(TelegramUser user) {
        super(user);
    }
}
