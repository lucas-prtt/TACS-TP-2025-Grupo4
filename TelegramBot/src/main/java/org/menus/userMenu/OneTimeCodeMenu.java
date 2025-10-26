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
import org.utils.ErrorHandler;

import java.util.Map;

public class OneTimeCodeMenu extends MenuState {
    String username;
    public OneTimeCodeMenu() {
        super();
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
            user.setMenu(new MainMenu());
            return user.getLocalizedMessage("successfulLogin", user.getServerAccountId(), user.getServerAccountUsername());
        }catch (HttpClientErrorException e){
            user.setMenu(new UserMenu());
            return ErrorHandler.getErrorMessage(e, user);
        }catch (ResourceAccessException e) {
            user.setMenu(new UserMenu());
            return user.getLocalizedMessage("serverUnavailable");
        }
        catch (Exception e){
            System.err.println(e.getMessage());
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
