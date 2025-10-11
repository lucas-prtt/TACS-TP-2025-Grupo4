package org.users;

import lombok.Getter;
import lombok.Setter;
import org.eventServerClient.ApiClient;
import org.menus.userMenu.UserMenu;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.menus.MainMenu;
import org.menus.MenuState;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.utils.I18nManager;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;


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
    private final List<QueryFilter> filtros = new ArrayList();
    @Setter
    private String lang = "en";
    @Setter
    private Locale userLocale = Locale.US;

    public TelegramUser(Long chatId){
        this.chatId = chatId;
        menu = new MainMenu(this);
        apiClient = ApiClient.withoutToken(this);
    }

    // Responde al menu en el que se encuentra y actualiza al siguiente menu si corresponde
    public SendMessage respondTo(Update update) {
        try {
            if (update.hasMessage()){
                if(update.getMessage().getText().equals("/start")){ // start manda al menu inicial no importa donde estes
                    this.menu = new MainMenu(this);
                    return null;
                }else{
                    return menu.responseMessage(update.getMessage());
                }
            }
            else if (update.hasCallbackQuery()){
                if(update.getCallbackQuery().getData().equals("/start")){ // start manda al menu inicial no importa donde estes
                    this.menu = new MainMenu(this);
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
        this.menu = menu;
        return menu.getQuestion();
    }
    public String setMainMenuAndRespond(){
        return setMenuAndRespond(new MainMenu(this));
    }
    public void addFilter(QueryFilter filter){
        filtros.add(filter);
    }
    public void clearFilters(){
        filtros.clear();
    }
    public String getAllFiltersAsQueryParams(){
        return "?" + String.join("&", filtros.toString());
    }
    public void updateApiClient(String token){
        setApiClient(ApiClient.fromToken(token, this));
    }
    public void updateUser(Map<String, Object> infoLogin){
        updateApiClient((String) infoLogin.get("token"));
        setServerAccountUsername((String) infoLogin.get("username"));
        setServerAccountId((String) infoLogin.get("id"));
        setMenu(new MainMenu(this));
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
        this.setServerAccountId(null);
        this.setServerAccountUsername(null);
        setApiClient(ApiClient.withoutToken(this));
        this.menu = new UserMenu(this);
    }

    public String getLocalizedMessage(String key, Object... args) {
        return I18nManager.get(key, this.lang, args);
    }
    public String localizeDate(LocalDateTime date){
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(userLocale);
        return date.format(formatter);
    }

}
