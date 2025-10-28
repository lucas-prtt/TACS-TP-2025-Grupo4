package org.utils.inputSteps;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.eventServerClient.dtos.event.EventDTO;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
@JsonTypeInfo(
        use = JsonTypeInfo.Id.MINIMAL_CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
public interface EventInputStep {
    SendMessage getQuestion(TelegramUser user);
    boolean handleInput(String message, EventDTO eventDTO, TelegramUser user);

}
