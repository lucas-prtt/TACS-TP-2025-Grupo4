package org.menus.organizerMenu;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eventServerClient.dtos.event.EventDTO;
import org.eventServerClient.dtos.event.EventStateDTO;
import org.menus.MenuState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.utils.InlineMenuBuilder;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class ManageEventMenu extends MenuState {
    EventDTO event;
    public ManageEventMenu(EventDTO item) {
        super();
        this.event = item;
    }

    @Override
    public String respondTo(String message) {
        switch (message){

            case "/cancel":
                if(event.getState() != EventStateDTO.EVENT_CANCELLED && !event.isPastDate()){
                    event.setState(EventStateDTO.EVENT_CANCELLED);
                    user.getApiClient().patchEventState(event.getId(), EventStateDTO.EVENT_CANCELLED);
                    return user.getLocalizedMessage("successfulCancel");
                }
                return user.getLocalizedMessage("wrongOption");

            case "/open":
                if(event.getState() == EventStateDTO.EVENT_CLOSED && !event.isPastDate())
                {
                    event.setState(EventStateDTO.EVENT_OPEN);
                    user.getApiClient().patchEventState(event.getId(), EventStateDTO.EVENT_OPEN);
                    return user.getLocalizedMessage("successfulOpen");
                }
                return user.getLocalizedMessage("wrongOption");
            case "/close":
                if(
                    event.getState() == EventStateDTO.EVENT_OPEN && !event.isPastDate())
                {
                    event.setState(EventStateDTO.EVENT_CLOSED);
                    user.getApiClient().patchEventState(event.getId(), EventStateDTO.EVENT_CLOSED);
                return user.getLocalizedMessage("successfulClose");
                }
                return user.getLocalizedMessage("wrongOption");
            case "/back":
                user.setMenu(new ManageEventSelectionMenu());
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
        /*if(event.getState() != EventStateDTO.EVENT_PAUSED){opciones.add("/pause");}*/
        if(event.getState() == EventStateDTO.EVENT_CLOSED && !event.isPastDate()){opciones.add("/open");}
        if(event.getState() == EventStateDTO.EVENT_OPEN && !event.isPastDate()){opciones.add("/close");}
        if((event.getState() == EventStateDTO.EVENT_OPEN || event.getState() == EventStateDTO.EVENT_CLOSED)  && !event.isPastDate()){opciones.add("/cancel");}

        opciones.add("/back");
        opciones.add("/start");
        return InlineMenuBuilder.localizedVerticalMenu(user, getQuestion(), opciones.toArray(new String[0]));
    }

}
