package org.utils.inputSteps;

import org.eventServerClient.dtos.event.EventDTO;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;

public interface EventInputStep {
    SendMessage getQuestion(TelegramUser user);
    boolean handleInput(String message, EventDTO eventDTO, TelegramUser user);

}
