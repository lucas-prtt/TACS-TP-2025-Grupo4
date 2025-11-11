package org.utils.inputSteps;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eventServerClient.dtos.event.CategoryDTO;
import org.eventServerClient.dtos.event.EventDTO;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.utils.categorySelectionMenus.EventBuildStepCategoryMenu;

import java.lang.reflect.Field;
@NoArgsConstructor
@Getter
@Setter
public class CategoryDTOInputStep implements EventInputStep{

    private String fieldName;
    private EventBuildStepCategoryMenu selectMenu;


    public CategoryDTOInputStep(String fieldName) {
        this.fieldName = fieldName;
        this.selectMenu = new EventBuildStepCategoryMenu();
    }

    @Override
    public SendMessage getQuestion(TelegramUser user) {
        selectMenu.setUser(user);
        selectMenu.setFinished(false);
        return selectMenu.questionMessageWithoutBackAndStart();
    }

    @Override
    public boolean handleInput(String message, EventDTO eventDTO, TelegramUser user) {
        try {
            selectMenu.setUser(user);
            selectMenu.respondTo(message);
            if(selectMenu.isFinished()){
                Field field = eventDTO.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(eventDTO, new CategoryDTO(selectMenu.getCategory()));
            }
            return selectMenu.isFinished();
        } catch (Exception e) {
            return false;
        }
    }


}
