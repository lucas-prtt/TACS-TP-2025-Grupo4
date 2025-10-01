package org.menus.userMenu;

import org.eventServerClient.dtos.AccountDTO;
import org.eventServerClient.dtos.LoginRequestDTO;
import org.springframework.web.client.HttpClientErrorException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.menus.MenuState;

import java.util.Map;

public class LoginUserMenu extends MenuState {
    LoginRequestDTO newUser = new LoginRequestDTO();
    @Override
    // Recibe el nombre del usuario y lo intenta crear. Si no puede devuelve un error. Si lo crea lo establece como el usado
    public String respondTo(String message) {
        try {
            if(newUser.getUsername() == null){
                newUser.setUsername(message);
                return null;
            }
            newUser.setPassword(message);
            Map<String, Object> res = user.getApiClient().loginUserAndPassword(newUser);
            user.updateUser(res);
            return "Cuenta Logueada\n" + "userID: " + user.getServerAccountId() + "\n"
                    + "username: " + user.getServerAccountUsername() + "\n";
        }catch (HttpClientErrorException e){
            System.out.println(e.getMessage());
            return "Error al crear usuario." + e.getMessage();
        }catch (Exception e){
            System.out.println(e.getMessage());
            user.setMenu(new UserMenu(user));
            return "Error al loguearse al usuario." + e.getMessage();
        }
    }
    @Override
    public SendMessage questionMessage() {
        SendMessage message = sendMessageText(getQuestion());
        return message;
    }
    @Override
    public String getQuestion() {
        if (newUser.getUsername() == null)
            return "Ingrese el nombre del usuario";
        else if (newUser.getPassword() == null)
            return "Ingrese la contraseña:";
        return user.setMainMenuAndRespond();
    }

    public LoginUserMenu(TelegramUser user) {
        super(user);
    }
}
