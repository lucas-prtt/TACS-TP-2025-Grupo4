package org.utils.inputSteps;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eventServerClient.dtos.event.EventDTO;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.utils.InlineMenuBuilder;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
@NoArgsConstructor
@Getter
@Setter
public class BigDecimalInputStep implements EventInputStep{
    private String fieldName;
    private String ceroValue;
    public BigDecimalInputStep(String fieldName, String ceroValue) {
        this.fieldName = fieldName;
        this.ceroValue = ceroValue;
    }

    @Override
    public SendMessage getQuestion(TelegramUser user) {
        return InlineMenuBuilder.menu(user.getLocalizedMessage("inputGeneric", user.getLocalizedMessage("the" + fieldName)), Map.of(user.getLocalizedMessage(ceroValue), "0"));
    }

    @Override
    public boolean handleInput(String message, EventDTO eventDTO, TelegramUser user) {
        try {
            BigDecimal value = new BigDecimal(message);
            Field field = eventDTO.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(eventDTO, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
