package org.utils.inputSteps;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eventServerClient.dtos.event.EventDTO;
import org.eventServerClient.dtos.event.TagDTO;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.utils.InlineMenuBuilder;

import java.util.List;
import java.util.Objects;
@NoArgsConstructor
@Getter
@Setter
public class TagsInputStep implements EventInputStep{

    private String fieldName;

    public TagsInputStep(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public SendMessage getQuestion(TelegramUser user) {

        return InlineMenuBuilder.localizedMenu(user, user.getLocalizedMessage("inputGeneric", user.getLocalizedMessage("the" + fieldName)), List.of("/stop"));
    }

    @Override
    public boolean handleInput(String message, EventDTO eventDTO, TelegramUser user) {
        try {
            if(Objects.equals(message, "/stop"))
                return true;
            eventDTO.getTags().add(new TagDTO(message));
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
