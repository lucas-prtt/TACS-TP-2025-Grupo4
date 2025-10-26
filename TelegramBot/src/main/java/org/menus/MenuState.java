package org.menus;

import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.users.TelegramUser;

public abstract class MenuState {
    abstract public String respondTo(String message);
    abstract public String getQuestion();
    public SendMessage responseMessage(Message message){
        return sendMessageText(respondTo(message.getText()));
    };
    public SendMessage responseMessage(CallbackQuery message){
        return sendMessageText(respondTo(message.getData()));
    };
    abstract public SendMessage questionMessage();
    @Setter
    protected TelegramUser user;
    public MenuState(){}
    protected SendMessage sendMessageTemplate(){
        return new SendMessage();
    }
    protected SendMessage sendMessageText(String text){
        if(text == null){return null;}
        SendMessage response = sendMessageTemplate();
        response.setText(text);
        return response;
    }
}
