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
    Queue<EventInputStep> inputs = new LinkedList<>();
    private EventInputStep currentStep;
    public NewEventMenu(TelegramUser user) {
        super(user);
        eventDTO = new EventDTO();
        eventDTO.setUsernameOrganizer(user.getServerAccountUsername());
        inputs.add(new StringInputStep("title", user.getLocalizedMessage("title")));
        inputs.add(new StringInputStep("description", user.getLocalizedMessage("description")));
        inputs.add(new DateTimeInputStep("startDateTime", user));
        inputs.add(new IntegerInputStep("durationMinutes", user.getLocalizedMessage("durationMinutes"), List.of(15, 30, 60, 120, 180)));
        inputs.add(new StringInputStep("location", user.getLocalizedMessage("location")));
        inputs.add(new IntegerInputStep("maxParticipants", user.getLocalizedMessage("maxParticipants"), List.of(10, 20, 50, 100, 500, 1000)));
        inputs.add(new IntegerInputStep("minParticipants", user.getLocalizedMessage("minParticipants"), List.of(0, 10, 20, 50, 100)));
        inputs.add(new BigDecimalInputStep("price", user.getLocalizedMessage("price"), "freePrice"));
        inputs.add(new CategoryDTOInputStep("category", user));
        inputs.add(new TagsInputStep("tags", user.getLocalizedMessage("tags")));
        currentStep = inputs.poll();
    }

    @Override
    public String respondTo(String message) {
        if (currentStep == null) return null;

        boolean handled = currentStep.handleInput(message, eventDTO, user);
        if (handled) {
            currentStep = inputs.poll();
            if (currentStep == null) {
                user.getApiClient().postEvent(eventDTO);
                user.setMenu(new MainMenu(user));
                return user.getLocalizedMessage("eventSuccesfullyCreated");
            }
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

