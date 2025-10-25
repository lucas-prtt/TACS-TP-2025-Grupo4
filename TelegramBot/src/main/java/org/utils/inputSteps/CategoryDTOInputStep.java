package org.utils.inputSteps;

import org.eventServerClient.dtos.event.CategoryDTO;
import org.eventServerClient.dtos.event.EventDTO;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.utils.I18nManager;
import org.utils.InlineMenuBuilder;
import org.utils.SelectCategoryMenu;

import java.lang.reflect.Field;
import java.util.List;

public class CategoryDTOInputStep implements EventInputStep{

    private final String fieldName;
    private final String label;
    private boolean finished = false;
    private String category = null;
    private final SelectCategoryMenu selectMenu;

    public CategoryDTOInputStep(String fieldName, String label, TelegramUser user) {
        this.fieldName = fieldName;
        this.label = label;
        this.selectMenu = new SelectCategoryMenu(user,
                (categoryDTO) -> {
                    this.category = categoryDTO.getTitle();
                    this.finished = true;
                },
                () -> null);
    }

    @Override
    public SendMessage getQuestion(TelegramUser user) {
        List<String> options = selectMenu.optionsList();
        String text = selectMenu.questionMessage().getText();
        return InlineMenuBuilder.localizedMenu(user, user.getLocalizedMessage("category").transform(I18nManager::capitalize) + " \n" + text, List.of("/prev", "/next"), options);
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
