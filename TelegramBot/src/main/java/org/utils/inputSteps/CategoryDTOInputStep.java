package org.utils.inputSteps;

import org.eventServerClient.dtos.event.CategoryDTO;
import org.eventServerClient.dtos.event.EventDTO;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;

import java.lang.reflect.Field;

public class CategoryDTOInputStep implements EventInputStep{

    private final String fieldName;
    private final String label;

    public CategoryDTOInputStep(String fieldName, String label) {
        this.fieldName = fieldName;
        this.label = label;
    }

    @Override
    public SendMessage getQuestion(TelegramUser user) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(user.getLocalizedMessage("inputGeneric", label));
        return sendMessage;    }

    @Override
    public boolean handleInput(String message, EventDTO eventDTO, TelegramUser user) {
        try {
            Field field = eventDTO.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(eventDTO, new CategoryDTO(message));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
