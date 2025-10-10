package org.menus.userMenu;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eventServerClient.ApiClient;
import org.eventServerClient.dtos.AccountDTO;
import org.menus.MainMenu;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.menus.MenuState;

import java.util.Map;

public class OneTimeCodeMenu extends MenuState {
    String username;
    public OneTimeCodeMenu(TelegramUser user) {
        super(user);
    }

    @Override
    public String respondTo(String message) {
        try {
            if(username == null){
                username = message;
                return null;
            }

            Map<String, Object> response = user.getApiClient().loginOneTimeCode(message, username);
            user.updateUser(response);
            user.setMenu(new MainMenu(user));
            return user.getLocalizedMessage("successfulLogin", user.getServerAccountId(), user.getServerAccountUsername());
        }catch (HttpClientErrorException e){
            System.out.println(e.getMessage());
            try {
                Map<String, String> errorMap = new ObjectMapper().readValue(e.getResponseBodyAsString(), Map.class);
                return e.getStatusCode().toString()+"\n" + errorMap.getOrDefault("error", user.getLocalizedMessage("unknownErrorInServer")) + "\n\n";
            } catch (Exception e2) {
                System.out.println(e2.getMessage());
                user.setMenu(new UserMenu(user));
                return user.getLocalizedMessage("unknownErrorInServer");
            }
        }catch (ResourceAccessException e) {
            System.out.println("Servidor no disponible: " + e.getMessage());
            user.setMenu(new UserMenu(user));
            return user.getLocalizedMessage("serverUnavailable");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return user.getLocalizedMessage("internalBotError");
        }
    }
    @Override
    public SendMessage questionMessage() {
        SendMessage message = sendMessageText(getQuestion());
        return message;
    }

    @Override
    public String getQuestion() {
        if(username == null){
            return user.getLocalizedMessage("requestInputUsername");
        }
        return user.getLocalizedMessage("requestInputOneTimeCode");
    }
}
