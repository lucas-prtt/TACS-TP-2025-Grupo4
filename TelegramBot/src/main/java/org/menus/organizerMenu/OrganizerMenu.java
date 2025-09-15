package org.menus.organizerMenu;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.menus.MenuState;
import org.utils.InlineMenuBuilder;

import java.util.List;

public class OrganizerMenu extends MenuState {
    @Override
    public String respondTo(String message) {
        switch (message){
            case "/newEvent":
                user.setMenu(new NewEventMenu(user));
                return null;
            case "/manageEvents":
                user.setMenu(new ManageEventSelectionMenu(user));
                return null;
            default:
                return "Error - opcion invalida\n";
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
    @Override
    public SendMessage questionMessage() {
        SendMessage message = InlineMenuBuilder.menu(getQuestion(), List.of("/newEvent", "/manageEvents", "start"));
        return message;
    }
}
