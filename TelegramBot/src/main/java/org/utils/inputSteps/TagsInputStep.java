package org.utils.inputSteps;

import org.eventServerClient.dtos.event.EventDTO;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.utils.InlineMenuBuilder;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

public class TagsInputStep implements EventInputStep{

    private final String fieldName;
    private final String label;

    public TagsInputStep(String fieldName, String label) {
        this.fieldName = fieldName;
        this.label = label;
    }

    @Override
    public SendMessage getQuestion(TelegramUser user) {

        return InlineMenuBuilder.localizedMenu(user, user.getLocalizedMessage("inputGeneric", label), List.of("/stop"));
    }

    @Override
    public boolean handleInput(String message, EventDTO eventDTO, TelegramUser user) {
        try {
            if(Objects.equals(message, "/stop"))
                return true;
            Field field = eventDTO.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(eventDTO, message);
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
