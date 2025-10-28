package org.utils.inputSteps;

import lombok.NoArgsConstructor;
import org.eventServerClient.dtos.event.EventDTO;
import org.menus.organizerMenu.OrganizerMenu;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.utils.InlineMenuBuilder;

import java.util.List;
@NoArgsConstructor
public class ConfirmCreationStep implements EventInputStep {
    @Override
    public SendMessage getQuestion(TelegramUser user) {
        return InlineMenuBuilder.localizedMenu(user, user.getLocalizedMessage("requestEventCreationConfirmation"), List.of("/confirm", "/cancel"));
    }

    @Override
    public boolean handleInput(String message, EventDTO eventDTO, TelegramUser user) {
        switch (message){
            case "/confirm":
                return true;
            case "/cancel":
                user.setMenu(new OrganizerMenu());
                return false;
        };
        return false;
    }
}
