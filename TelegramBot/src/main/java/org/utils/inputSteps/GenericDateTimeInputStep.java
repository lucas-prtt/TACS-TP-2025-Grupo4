package org.utils.inputSteps;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eventServerClient.dtos.event.EventDTO;
import org.exceptions.DateAlreadySetException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.utils.DateInputHelper;

import java.lang.reflect.Field;
@NoArgsConstructor
@Getter
@Setter
public class GenericDateTimeInputStep {
    private String fieldName;
    private DateInputHelper helper;


    public GenericDateTimeInputStep(String fieldName) {
        this.fieldName = fieldName;
        this.helper = new DateInputHelper();
    }


    public SendMessage getQuestion(TelegramUser user) {
        return helper.questionMessage(user);
    }

    public boolean handleInput(String input, Object thing, TelegramUser user) {
        try {

            if (!helper.respondTo(input, user)) {
                // Aún se está procesando la fecha, no pasar al siguiente paso
                return false;
            }
            Field field = thing.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(thing, helper.getDate().toString());
            return true; // Fecha completa y válida, pasar al siguiente paso
        } catch (DateAlreadySetException e) {
            return true; // Fecha ya estaba configurada (No deberia pasar)
        } catch (Exception e) {
            return false;
        }
    }
}
