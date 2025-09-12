package org.menus.organizerMenu;

import org.eventServerClient.ApiClient;
import org.eventServerClient.dtos.event.EventDTO;
import org.eventServerClient.dtos.event.EventStateDTO;
import org.menus.MenuState;
import org.users.TelegramUser;

public class ManageEventMenu extends MenuState {
    EventDTO event;
    public ManageEventMenu(TelegramUser user, EventDTO item) {
        super(user);
        this.event = item;
    }

    @Override
    public String respondTo(String message) {
        switch (message){
            case "/pause":
                event.setState(EventStateDTO.EVENT_PAUSED);
                ApiClient.patchEventState(event.getId(), EventStateDTO.EVENT_PAUSED);
                return "Evento pausado\n" + getQuestion();
            case "/open":
                event.setState(EventStateDTO.EVENT_OPEN);
                ApiClient.patchEventState(event.getId(), EventStateDTO.EVENT_OPEN);
                return "Evento cancelado\n" + getQuestion();
            case "/close":
                event.setState(EventStateDTO.EVENT_CLOSED);
                ApiClient.patchEventState(event.getId(), EventStateDTO.EVENT_CLOSED);
                return "Evento abierto\n" + getQuestion();
            case "/back":
                return user.setMenuAndRespond(new ManageEventSelectionMenu(user));
            default:
                return "Error, intente de nuevo" + getQuestion();
        }
    }

    @Override
    public String getQuestion() {
        return event.asDetailedString()  + "\n\n" +
                (event.getState() == EventStateDTO.EVENT_PAUSED ? "" : "/pause --> Pausar el evento\n")+
                (event.getState() == EventStateDTO.EVENT_OPEN ? "" : "/open --> Reabrir el evento\n")+
                (event.getState() == EventStateDTO.EVENT_CLOSED ? "" : "/close --> Cerrar el evento\n")+
                "/back --> Volver al menu anterior\n" +
                "/start --> Volver al menu principal";
    }
}
