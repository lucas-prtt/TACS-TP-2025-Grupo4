package org.menus.userMenu;

import lombok.Getter;
import lombok.Setter;
import org.eventServerClient.dtos.AccountDTO;
import org.eventServerClient.dtos.LoginRequestDTO;
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
public class RegisterUserMenu extends MenuState {
    AccountDTO newUser = new AccountDTO();
    @Override
    // Recibe el nombre del usuario y lo intenta crear. Si no puede devuelve un error. Si lo crea lo establece como el usado
    public String respondTo(String message) {
        if(Objects.equals(message, "/back"))
        {
            if(newUser.getUsername() == null){
                user.setMenu(new UserMenu());
                return null;
            }
            else {
                newUser.setUsername(null);
                return null;
            }
        }
        try {
            if(newUser.getUsername() == null){
                if(user.getApiClient().userExists(message)){
                    return user.getLocalizedMessage("usernameAlreadyUsed");
                }
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
            String errorCode = ErrorHandler.getErrorCode(e);
            if(Objects.equals(errorCode, "ERROR_WEAK_PASSWORD")){
                newUser.setPassword(null);
            }else if(Objects.equals(errorCode, "ERROR_USER_ALREADY_EXISTS")){
                newUser.setPassword(null);
                newUser.setUsername(null);
            }else {
                user.setMenu(new UserMenu());
            }
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
