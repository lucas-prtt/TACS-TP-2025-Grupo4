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
import java.util.Map;
@NoArgsConstructor
@Getter
@Setter
public class BigDecimalInputStep implements EventInputStep{
    private String fieldName;
    private String ceroValue;
    private Integer min;
    private Integer max;
    /**
     * @param  min  handleInput = false si el valor es menor a min. Acepta null
     * @param  max  handleInput = false si el valor es mayor a max. Acepta null
     */
    public BigDecimalInputStep(String fieldName, String ceroValue, Integer min, Integer max) {
        this.fieldName = fieldName;
        this.ceroValue = ceroValue;
        this.min = min;
        this.max = max;
    }

    @Override
    public SendMessage getQuestion(TelegramUser user) {
        return InlineMenuBuilder.menu(user.getLocalizedMessage("inputGeneric", user.getLocalizedMessage("the" + fieldName)), Map.of(user.getLocalizedMessage(ceroValue), "0"));
    }

    @Override
    public boolean handleInput(String message, EventDTO eventDTO, TelegramUser user) {
        try {
            BigDecimal value = new BigDecimal(message);
            if ((min != null && value.compareTo(BigDecimal.valueOf(min)) < 0) ||
                    (max != null && value.compareTo(BigDecimal.valueOf(max)) > 0)) {
                return false;
            }
            Field field = eventDTO.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(eventDTO, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
