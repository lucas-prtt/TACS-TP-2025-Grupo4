package org.menus.userMenu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.eventServerClient.ApiClient;
import org.eventServerClient.dtos.AccountDTO;
import org.eventServerClient.dtos.LoginRequestDTO;
import org.menus.MainMenu;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.menus.MenuState;
import org.utils.ErrorHandler;
import org.utils.InlineMenuBuilder;

import java.util.Map;
import java.util.Objects;

@Getter
@Setter
public class RegisterUserMenu extends MenuState {
    AccountDTO newUser = new AccountDTO();
    @Override
    // Recibe el nombre del usuario y lo intenta crear. Si no puede devuelve un error. Si lo crea lo establece como el usado
    public String respondTo(String message) {
        if(Objects.equals(message, "/back"))
        {
            user.setMenu(new UserMenu());
        }
        try {
            if(newUser.getUsername() == null){
                newUser.setUsername(message);
                return null;
            }
            newUser.setPassword(message);
            AccountDTO usuarioCreado = user.getApiClient().postAccount(newUser.getUsername(), newUser.getPassword());
            user.setMenu(new MainMenu());
            Map<String, Object> res = user.getApiClient().loginUserAndPassword(new LoginRequestDTO(newUser.getUsername(), newUser.getPassword()));
            user.updateUser(res);
            return user.getLocalizedMessage("successfulRegister", usuarioCreado.getUuid());
        }catch (HttpClientErrorException e){
            user.setMenu(new UserMenu());
            return ErrorHandler.getErrorMessage(e, user);
        }catch (ResourceAccessException e) {
            user.setMenu(new UserMenu());
            return user.getLocalizedMessage("serverUnavailable");
        }
        catch (Exception e){
            System.err.println(e.getMessage());
            user.setMenu(new UserMenu());
            return user.getLocalizedMessage("internalBotError");
        }
    }

    @Override
    public String getQuestion() {
        if (newUser.getUsername() == null)
        return user.getLocalizedMessage("requestInputUsername");
        else if (newUser.getPassword() == null)
            return user.getLocalizedMessage("requestInputPassword");
        return user.setMainMenuAndRespond();
    }

    @Override
    public SendMessage questionMessage() {
        SendMessage message = InlineMenuBuilder.localizedVerticalMenu(user, getQuestion(), "/back");
        return message;
    }

    public RegisterUserMenu() {
        super();
    }
}
