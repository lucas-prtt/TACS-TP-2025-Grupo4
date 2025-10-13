package org.utils.inputSteps;

import org.eventServerClient.dtos.event.EventDTO;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.utils.InlineMenuBuilder;

import java.lang.reflect.Field;
import java.util.List;

public class IntegerInputStep implements EventInputStep{
    private final String fieldName;
    private final String label;
    private List<Integer> defaultOptions;
    public IntegerInputStep(String fieldName, String label, List<Integer> defaultOptions) {
        this.fieldName = fieldName;
        this.label = label;
        this.defaultOptions = defaultOptions;
    }

    @Override
    public SendMessage getQuestion(TelegramUser user) {
        return InlineMenuBuilder.menu(user.getLocalizedMessage("inputGeneric", label), defaultOptions.stream().map(Object::toString).toList());
    }

    @Override
    public boolean handleInput(String message, EventDTO eventDTO, TelegramUser user) {
        try {
            int value = Integer.parseInt(message);
            Field field = eventDTO.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(eventDTO, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
