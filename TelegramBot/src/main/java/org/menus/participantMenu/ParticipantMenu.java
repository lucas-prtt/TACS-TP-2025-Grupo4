package org.menus.participantMenu;

import org.users.TelegramUser;
import org.menus.MenuState;

public class ParticipantMenu extends MenuState {
    @Override
    public String respondTo(String message) {
        return "";
    }

    @Override
    public String getQuestion() {
        return """
                Menu de participante:
                
                /registerToEvent
                    - Registrarse o unirse a la waitlist para el evento dada la UUID del evento
                /getRegisteredEvents
                    - Ver eventos a los que esta registrado
                /getWaitLists
                    - Ver eventos a los que esta en la wishlist
                /deleteRegistration
                    - Eliminar registro a un evento dada la UUID de registro
                
                """;
    }

    public ParticipantMenu(TelegramUser user) {
        super(user);
    }
}
