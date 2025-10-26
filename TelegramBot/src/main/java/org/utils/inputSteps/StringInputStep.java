package org.utils.inputSteps;

import org.eventServerClient.dtos.event.EventDTO;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;

import java.lang.reflect.Field;

public class StringInputStep implements EventInputStep{

    private final String fieldName;

    public StringInputStep(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public SendMessage getQuestion(TelegramUser user) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(user.getLocalizedMessage("inputGeneric", user.getLocalizedMessage(fieldName)));
        return sendMessage;
    }

    @Override
    public boolean handleInput(String message, EventDTO eventDTO, TelegramUser user) {
        try {
            Field field = eventDTO.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(eventDTO, message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
