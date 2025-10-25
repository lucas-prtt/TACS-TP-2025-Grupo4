package org.utils.inputSteps;

import org.eventServerClient.dtos.event.CategoryDTO;
import org.eventServerClient.dtos.event.EventDTO;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.utils.I18nManager;
import org.utils.InlineMenuBuilder;
import org.utils.SelectCategoryMenu;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CategoryDTOInputStep implements EventInputStep{

    private final String fieldName;
    private boolean finished = false;
    private String category = null;
    private final SelectCategoryMenu selectMenu;


    public CategoryDTOInputStep(String fieldName, TelegramUser user) {
        this.fieldName = fieldName;
        this.selectMenu = new SelectCategoryMenu(user,
                (categoryDTO) -> {
                    this.category = categoryDTO.getTitle();
                    this.finished = true;
                },
                () -> null);
    }

    @Override
    public SendMessage getQuestion(TelegramUser user) {
        return selectMenu.questionMessageWithoutBackAndStart();
    }

    @Override
    public boolean handleInput(String message, EventDTO eventDTO, TelegramUser user) {
        try {
            selectMenu.respondTo(message);
            if(finished){
                Field field = eventDTO.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(eventDTO, new CategoryDTO(category));
            }
            return finished;
        } catch (Exception e) {
            return false;
        }
    }


}
