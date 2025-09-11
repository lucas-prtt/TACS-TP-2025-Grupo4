package org.menus.participantMenu;

import org.menus.organizerMenu.ManageEventsMenu;
import org.menus.organizerMenu.NewEventMenu;
import org.users.TelegramUser;
import org.menus.MenuState;

public class ParticipantMenu extends MenuState {
    @Override
    public String respondTo(String message) {
        switch (message){
            case "/registerToEvent":
                return user.setMenuAndRespond(new RegisterToEventMenu(user)); //TODO
            case "/getRegisteredEvents":
                return user.setMenuAndRespond(new GetRegisteredEventsMenu(user)); //TODO
            case "/deleteRegistration":
                return user.setMenuAndRespond(new DeleteRegistrationMenu(user));  //TODO
            default:
                return "Error - opcion invalida\n" + user.getMenu().getQuestion();
        }
    }

    @Override
    public String getQuestion() {
        return """
                Menu de participante:
                
                /registerToEvent
                    - Registrarse o unirse a la waitlist para el evento dada la UUID del evento
                /getRegisteredEvents
                    - Ver eventos a los que esta registrado como participante o a la waitlist
                /deleteRegistration
                    - Eliminar registro a un evento dada la UUID de registro
                /start
                    - Volver al menú principal
                """;
    }

    public ParticipantMenu(TelegramUser user) {
        super(user);
    }
}
