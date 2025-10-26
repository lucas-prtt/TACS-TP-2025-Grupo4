package org.menus.organizerMenu;

import org.eventServerClient.ApiClient;
import org.eventServerClient.dtos.event.CategoryDTO;
import org.eventServerClient.dtos.event.EventDTO;
import org.eventServerClient.dtos.event.TagDTO;
import org.exceptions.DateAlreadySetException;
import org.menus.MainMenu;
import org.menus.MenuState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.utils.DateInputHelper;
import org.utils.inputSteps.*;
import org.yaml.snakeyaml.util.Tuple;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

public class NewEventMenu extends MenuState {
    EventDTO eventDTO;
    List<EventInputStep> inputs = new LinkedList<>();
    private EventInputStep currentStep;
    public NewEventMenu() {
        super();
        eventDTO = new EventDTO();
        inputs.add(new StringInputStep("title"));
        inputs.add(new StringInputStep("description"));
        inputs.add(new DateTimeInputStep("startDateTime"));
        inputs.add(new IntegerInputStep("durationMinutes", List.of(15, 30, 60, 120, 180)));
        inputs.add(new StringInputStep("location"));
        inputs.add(new IntegerInputStep("maxParticipants", List.of(10, 20, 50, 100, 500, 1000)));
        inputs.add(new IntegerInputStep("minParticipants", List.of(0, 10, 20, 50, 100)));
        inputs.add(new BigDecimalInputStep("price", "freePrice"));
        inputs.add(new CategoryDTOInputStep("category"));
        inputs.add(new TagsInputStep("tags"));
        currentStep = inputs.removeFirst();
    }

    @Override
    public String respondTo(String message) {
        if (currentStep == null) return null;

        boolean handled = currentStep.handleInput(message, eventDTO, user);
        if (handled) {
            if (inputs.isEmpty()) {
                user.getApiClient().postEvent(eventDTO);
                user.setMenu(new MainMenu());
                return user.getLocalizedMessage("eventSuccesfullyCreated");
            }
            currentStep = inputs.removeFirst();
        }
        return null;
    }

    @Override
    public String getQuestion() {
        return " - ";
    }
    @Override
    public SendMessage questionMessage() {
        if (currentStep == null) return user.getQuestion();
        return currentStep.getQuestion(user);
    }
}

