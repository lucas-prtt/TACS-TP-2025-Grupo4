package org.menus.organizerMenu;

import org.eventServerClient.ApiClient;
import org.eventServerClient.dtos.event.EventDTO;
import org.eventServerClient.dtos.event.EventStateDTO;
import org.menus.MenuState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.utils.InlineMenuBuilder;

import java.util.ArrayList;
import java.util.List;

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
                user.getApiClient().patchEventState(event.getId(), EventStateDTO.EVENT_PAUSED);
                return "Evento pausado\n";
            case "/open":
                event.setState(EventStateDTO.EVENT_OPEN);
                user.getApiClient().patchEventState(event.getId(), EventStateDTO.EVENT_OPEN);
                return "Evento cancelado\n";
            case "/close":
                event.setState(EventStateDTO.EVENT_CLOSED);
                user.getApiClient().patchEventState(event.getId(), EventStateDTO.EVENT_CLOSED);
                return "Evento abierto\n";
            case "/back":
                user.setMenu(new ManageEventSelectionMenu(user));
                return null;
            default:
                return "Error, intente de nuevo";
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
    @Override
    public SendMessage questionMessage() {
        List<String> opciones = new ArrayList<>();
        if(event.getState() != EventStateDTO.EVENT_PAUSED){opciones.add("/pause");}
        if(event.getState() != EventStateDTO.EVENT_OPEN){opciones.add("/open");}
        if(event.getState() != EventStateDTO.EVENT_CLOSED){opciones.add("/close");}
        SendMessage message = InlineMenuBuilder.menu(getQuestion(), opciones ,List.of("/back", "/start"));
        return message;
    }

}
