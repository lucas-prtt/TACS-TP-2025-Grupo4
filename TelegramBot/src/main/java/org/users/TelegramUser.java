package org.users;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.users.states.MainMenu;
import org.users.states.MenuState;

@Getter
public class TelegramUser {
    private final Long chatId;
    @Setter
    private String serverAccountId;
    @Setter
    private MenuState menu;

    public TelegramUser(Long chatId){
        this.chatId = chatId;
        menu = new MainMenu(this);
    }

    public SendMessage respondTo(Message message) {
        String chatId = message.getChatId().toString();
        SendMessage response = new SendMessage();
        response.setChatId(chatId);
        if(message.getText().equals("/start")){
            response.setText(this.setMenuAndRespond(new MainMenu(this)));
        }else{
            response.setText(menu.respondTo(message.getText()));
        }

        return response;
    }
    public String setMenuAndRespond(MenuState menu){
        this.menu = menu;
        return menu.getQuestion();
    }
    public String setMainMenuAndRespond(){
        return setMenuAndRespond(new MainMenu(this));
    }

}
