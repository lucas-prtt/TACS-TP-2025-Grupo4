package org.utils.inputSteps;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eventServerClient.dtos.event.EventDTO;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.utils.InlineMenuBuilder;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Consumer;

@NoArgsConstructor
@Getter
@Setter
public class IntegerInputStep implements EventInputStep{
    private String fieldName;
    private List<Integer> defaultOptions;
    private Integer min;
    private Integer max;
    /**
     * @param  min  handleInput = false si el valor es menor o igual a min. Acepta null
     * @param  max  handleInput = false si el valor es mayor o igual a max. Acepta null
     */
    public IntegerInputStep(String fieldName, List<Integer> defaultOptions, Integer min, Integer max) {
        this.fieldName = fieldName;
        this.defaultOptions = defaultOptions;
        this.min = min;
        this.max = max;
    }

    @Override
    public SendMessage getQuestion(TelegramUser user) {
        return InlineMenuBuilder.menu(user.getLocalizedMessage("inputGeneric", user.getLocalizedMessage("the" + fieldName)), defaultOptions.stream().map(Object::toString).toList());
    }

    @Override
    public boolean handleInput(String message, EventDTO eventDTO, TelegramUser user) {
        try {
            int value = Integer.parseInt(message);
            if((min != null && value<=min) || (max != null && value>=max)){
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
