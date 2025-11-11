package org.menus.browseMenu;

import org.eventServerClient.dtos.event.EventDTO;
import org.menus.MenuState;
import org.springframework.web.client.HttpClientErrorException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.utils.ErrorHandler;
import org.utils.InlineMenuBuilder;

import java.util.Objects;
import java.util.UUID;

public class LookUpEventByUUIDMenu extends MenuState {
    @Override
    public String respondTo(String message) {
        if(Objects.equals(message, "/back")){
            user.setMenu(new BrowseEventsMenu());
        }
        EventDTO eventDTO;
        try {
             eventDTO = user.getApiClient().getEvent(UUID.fromString(message));
        }catch (IllegalArgumentException e){
            return user.getLocalizedMessage("invalidUUID");
        }
        try {
            user.setMenu(new CheckEventMenu(eventDTO, this));
            return user.getLocalizedMessage("eventFound");
        }catch (HttpClientErrorException e){
            return ErrorHandler.getErrorMessage(e, user);
        }
    }

    @Override
    public String getQuestion() {
        return user.getLocalizedMessage("requestInputEventID");
    }

    public LookUpEventByUUIDMenu() {
        super();
    }
    @Override
    public SendMessage questionMessage() {
        SendMessage message = InlineMenuBuilder.localizedVerticalMenu(user, getQuestion(), "/back");
        return message;
    }
}
