package org.menus.organizerMenu;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.menus.MenuState;
import org.utils.InlineMenuBuilder;

import java.util.List;
import java.util.Map;

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
                
                Nuevo evento
                    - Crear y publicar un nuevo evento
                Gestionar eventos
                    - Ver y modificar estado de sus eventos
                Volver al inicio
                    - Volver al menú principal
                
                """;
    }

    public OrganizerMenu(TelegramUser user) {
        super(user);
    }
    @Override
    public SendMessage questionMessage() {
        SendMessage message = InlineMenuBuilder.menu(getQuestion(), Map.of("Nuevo evento", "/newEvent", "Gestionar eventos", "/manageEvents", "Volver al inicio", "/start"));
        return message;
    }
}
