package org.utils.inputSteps;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eventServerClient.dtos.event.EventDTO;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;

import java.lang.reflect.Field;
@NoArgsConstructor
@Getter
@Setter
public class StringInputStep implements EventInputStep{

    private String fieldName;

    public StringInputStep(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public SendMessage getQuestion(TelegramUser user) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(user.getLocalizedMessage("inputGeneric", user.getLocalizedMessage("the" + fieldName)));
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
