package org.menus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.users.TelegramUser;
@JsonTypeInfo(
        use = JsonTypeInfo.Id.MINIMAL_CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
public abstract class MenuState {
    abstract public String respondTo(String message);
    @JsonIgnore
    abstract public String getQuestion();
    public SendMessage responseMessage(Message message){
        return sendMessageText(respondTo(message.getText()));
    };
    public SendMessage responseMessage(CallbackQuery message){
        return sendMessageText(respondTo(message.getData()));
    };
    abstract public SendMessage questionMessage();
    @Setter
    @JsonIgnore
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
