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
    private final String label;
    private boolean finished = false;
    private String category = null;
    private final SelectCategoryMenu selectMenu;
    private static final Set<String> LETTERS = "abcdefghijklmnñopqrstuvwxyz<"
            .chars()
            .mapToObj(c -> String.valueOf((char)c))
            .collect(Collectors.toUnmodifiableSet());

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
        String text = selectMenu.getQuestion();
        return InlineMenuBuilder.localizedMenu(user, label.transform(I18nManager::capitalize) + "\n" +  user.getLocalizedMessage("writeCategoryToFilter") + " \n" + text, List.of("/prev", "/next"), options, !Objects.equals(selectMenu.getStartsWith(), "") ?List.of("/eraseStartsWith") : List.of());
    }

    @Override
    public boolean handleInput(String message, EventDTO eventDTO, TelegramUser user) {
        try {
            String fstChar = message.substring(0, 1);
            if(isLetter(fstChar)) // No empieza con / (/page, /next, etc) ni con numero (opcion elegida)
            {
                selectMenu.setStartsWith(String.copyValueOf(message.toCharArray()));
                return false;
            }
            if(message.equals("/eraseStartsWith")){
                selectMenu.setStartsWith("");
                return false;
            }
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
    private boolean isLetter(String character) {
        return character != null
                && character.length() == 1
                && LETTERS.contains(character.toLowerCase());
    }

}
