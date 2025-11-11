package org.menus.userMenu;
import lombok.Getter;
import lombok.Setter;
import org.menus.MainMenu;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.menus.MenuState;
import org.utils.ErrorHandler;
import org.utils.InlineMenuBuilder;

import java.util.Map;
import java.util.Objects;

@Getter
@Setter
public class OneTimeCodeMenu extends MenuState {
    String username;
    public OneTimeCodeMenu() {
        super();
    }

    @Override
    public String respondTo(String message) {
        if(Objects.equals(message, "/back"))
        {
            user.setMenu(new UserMenu());
        }
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
        SendMessage message = InlineMenuBuilder.localizedVerticalMenu(user, getQuestion(), "/back");
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
