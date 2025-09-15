package org.users;

import lombok.Getter;
import lombok.Setter;
import org.eventServerClient.ApiClient;
import org.springframework.http.HttpHeaders;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.menus.MainMenu;
import org.menus.MenuState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Getter
public class TelegramUser {
    @Getter
    @Setter
    private ApiClient apiClient;
    @Getter
    private final Long chatId;
    @Setter
    private String serverAccountId;
    @Setter
    private String serverAccountUsername;
    @Setter
    private MenuState menu;
    private final List<String> filtros = new ArrayList();


    public TelegramUser(Long chatId){
        this.chatId = chatId;
        menu = new MainMenu(this);
    }

    // Responde al menu en el que se encuentra y actualiza al siguiente menu si corresponde
    public SendMessage respondTo(Message message) {
        String chatId = message.getChatId().toString();
        SendMessage response = new SendMessage();
        response.setChatId(chatId);
        if(message.getText().equals("/start")){ // start manda al menu inicial no importa donde estes
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
    public void addFilter(String filter){
        filtros.add(filter);
    }
    public void clearFilters(){
        filtros.clear();
    }
    public String getAllFiltersAsQueryParams(){
        return "?" + String.join("&", filtros);
    }
    public void updateApiClient(String token){
        setApiClient(ApiClient.fromToken(token));
    }
    public void updateUser(Map<String, Object> infoLogin){
        updateApiClient((String) infoLogin.get("token"));
        setServerAccountUsername((String) infoLogin.get("username"));
        setServerAccountId((String) infoLogin.get("id"));
    }
}
