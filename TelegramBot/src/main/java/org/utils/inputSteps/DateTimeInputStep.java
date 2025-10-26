package org.utils.inputSteps;

import org.eventServerClient.dtos.event.EventDTO;
import org.exceptions.DateAlreadySetException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.utils.DateInputHelper;

import java.lang.reflect.Field;

public class DateTimeInputStep implements EventInputStep {
    private final String fieldName;
    private final DateInputHelper helper;


    public DateTimeInputStep(String fieldName) {
        this.fieldName = fieldName;
        this.helper = new DateInputHelper();
    }


    @Override
    public SendMessage getQuestion(TelegramUser user) {
        return helper.questionMessage(user);
    }

    @Override
    public boolean handleInput(String input, EventDTO event, TelegramUser user) {
        try {

            if (!helper.respondTo(input, user)) {
                // Aún se está procesando la fecha, no pasar al siguiente paso
                return false;
            }
            Field field = event.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(event, helper.getDate());
            return true; // Fecha completa y válida, pasar al siguiente paso
        } catch (DateAlreadySetException e) {
            return true; // Fecha ya estaba configurada (No deberia pasar)
        } catch (Exception e) {
            return false;
        }
    }
}
