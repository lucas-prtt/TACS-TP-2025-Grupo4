package org.menus.organizerMenu;

import org.eventServerClient.ApiClient;
import org.eventServerClient.dtos.event.EventDTO;
import org.eventServerClient.dtos.event.EventStateDTO;
import org.menus.MenuState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.utils.InlineMenuBuilder;

import java.lang.reflect.Array;
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
                return user.getLocalizedMessage("successfulPause");
            case "/open":
                event.setState(EventStateDTO.EVENT_OPEN);
                user.getApiClient().patchEventState(event.getId(), EventStateDTO.EVENT_OPEN);
                return user.getLocalizedMessage("successfulOpen");
            case "/close":
                event.setState(EventStateDTO.EVENT_CLOSED);
                user.getApiClient().patchEventState(event.getId(), EventStateDTO.EVENT_CLOSED);
                return user.getLocalizedMessage("successfulClose");
            case "/back":
                user.setMenu(new ManageEventSelectionMenu(user));
                return null;
            default:
                return user.getLocalizedMessage("wrongOption");
        }
    }

    @Override
    public String getQuestion() {
        return event.asDetailedString(user);
    }
    @Override
    public SendMessage questionMessage() {
        List<String> opciones = new ArrayList<>();
        if(event.getState() != EventStateDTO.EVENT_PAUSED){opciones.add("/pause");}
        if(event.getState() != EventStateDTO.EVENT_OPEN){opciones.add("/open");}
        if(event.getState() != EventStateDTO.EVENT_CLOSED){opciones.add("/close");}
        opciones.add("/back");
        opciones.add("/start");
        return InlineMenuBuilder.localizedVerticalMenu(user, getQuestion(), opciones.toArray(new String[0]));
    }

}
