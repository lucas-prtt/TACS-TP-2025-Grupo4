package org.users;

import lombok.Getter;
import lombok.Setter;
import org.eventServerClient.ApiClient;
import org.menus.userMenu.UserMenu;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.menus.MainMenu;
import org.menus.MenuState;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.utils.I18nManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;


@Getter
public class TelegramUser {
    @Getter
    private final Long chatId;
    @Setter
    private String serverAccountId;
    @Setter
    private String serverAccountUsername;
    @Setter
    private String token;
    private MenuState menu;
    private final List<QueryFilter> filtros = new ArrayList();
    @Setter
    private String lang = "en";
    @Setter
    private Locale userLocale = Locale.US;
    @Setter
    private List<String> roles;

    public TelegramUser(Long chatId){
        this.chatId = chatId;
        this.setMenu(new MainMenu());
    }

    public void setMenu(MenuState menu){
        this.menu = menu;
        menu.setUser(this);
    }

    // Responde al menu en el que se encuentra y actualiza al siguiente menu si corresponde
    public SendMessage respondTo(Update update) {
        try {
            if (update.hasMessage()){
                if(update.getMessage().getText().equals("/start")){ // start manda al menu inicial no importa donde estes
                    setMenu(new MainMenu());
                    return null;
                }else{
                    return menu.responseMessage(update.getMessage());
                }
            }
            else if (update.hasCallbackQuery()){
                if(update.getCallbackQuery().getData().equals("/start")){ // start manda al menu inicial no importa donde estes
                   setMenu(new MainMenu());
                    return null;
                }else{
                    return menu.responseMessage(update.getCallbackQuery());
                }
            }
        }
        catch (HttpClientErrorException e){
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED){
                SendMessage rta =  new SendMessage();
                rta.setText("Se vencio el token");
                return rta;
            }
        }catch (Exception e){
            e.printStackTrace();
            SendMessage rta =  new SendMessage();
            rta.setText("Se produjo un error interno al responder");
            return rta;
        }
        System.out.println("Error al responder");
        return null;
    }
    public String setMenuAndRespond(MenuState menu){
        this.setMenu(menu);
        return menu.getQuestion();
    }
    public String setMainMenuAndRespond(){
        return setMenuAndRespond(new MainMenu());
    }
    public void addFilter(QueryFilter filter){
        filtros.add(filter);
    }
    public void clearFilters(){
        filtros.clear();
    }
    public String getAllFiltersAsQueryParams(){
        return "?" + String.join("&", filtros.stream().map(Object::toString).toList());
    }

    public void updateUser(Map<String, Object> infoLogin){
        this.token = (String) infoLogin.get("token");
        this.serverAccountUsername = ((String) infoLogin.get("username"));
        this.serverAccountId = ((String) infoLogin.get("id"));
        try {
            setRoles((List<String>) infoLogin.get("roles"));
        }catch (Exception e){
            setRoles(List.of("USER"));
        }
        setMenu(new MainMenu());
    }

    public Boolean isAdmin(){
        return roles != null && roles.contains("ADMIN");
    }
    public Boolean isUser(){
        return roles != null && roles.contains("USER");
    }

    public SendMessage getQuestion() {
        try{
            return menu.questionMessage();
        }
        catch (HttpClientErrorException e){
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED){
                SendMessage rta =  new SendMessage();
                rta.setText("Se vencio el token\n" + getMenu().getQuestion());
                return rta;
            }
        }catch (Exception e){
            e.printStackTrace();
            SendMessage rta =  new SendMessage();
            rta.setText("Se produjo un error interno al responder");
            return rta;
        }
        System.out.println("Error al responder");
        return null;
    }

    public void deleteCurrentAccount(){
        this.serverAccountId = null;
        this.serverAccountUsername = null;
        this.token = null;
        this.roles = null;
        setMenu(new UserMenu());
    }

    public String getLocalizedMessage(String key, Object... args) {
        return I18nManager.get(key, this.lang, args);
    }
    public String localizeDate(LocalDateTime date){
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(userLocale);
        return date.format(formatter);
    }
    public ApiClient getApiClient(){
        return new ApiClient(token, this);
    }

}
