package org.menus.userMenu;

import org.eventServerClient.ApiClient;
import org.eventServerClient.dtos.AccountDTO;
import org.menus.MainMenu;
import org.springframework.web.client.HttpClientErrorException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.menus.MenuState;

public class RegisterUserMenu extends MenuState {
    AccountDTO newUser = new AccountDTO();
    @Override
    // Recibe el nombre del usuario y lo intenta crear. Si no puede devuelve un error. Si lo crea lo establece como el usado
    public String respondTo(String message) {
        try {
            if(newUser.getUsername() == null){
                newUser.setUsername(message);
                return null;
            }
            newUser.setPassword(message);
            AccountDTO usuarioCreado = user.getApiClient().postAccount(newUser.getUsername(), newUser.getPassword());
            user.setMenu(new MainMenu(user));
            return "Cuenta creada\n" + "userID: " + usuarioCreado.getUuid() + "\n";
        }catch (HttpClientErrorException e){
            System.out.println(e.getMessage());
            user.setMenu(new UserMenu(user));
            return "Error al crear usuario." + e.getResponseBodyAsString();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            user.setMenu(new UserMenu(user));
            return "Error al crear usuario." + e.getMessage();
        }
    }

    @Override
    public String getQuestion() {
        if (newUser.getUsername() == null)
        return "Ingrese el nombre del usuario a crear";
        else if (newUser.getPassword() == null)
            return "Ingrese la contraseña:";
        return user.setMainMenuAndRespond();
    }

    @Override
    public SendMessage questionMessage() {
        SendMessage message = sendMessageText(getQuestion());
        return message;
    }

    public RegisterUserMenu(TelegramUser user) {
        super(user);
    }
}
