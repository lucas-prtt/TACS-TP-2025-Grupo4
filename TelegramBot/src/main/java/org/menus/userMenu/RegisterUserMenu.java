package org.menus.userMenu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
            try {
                Map<String, String> errorMap = new ObjectMapper().readValue(e.getResponseBodyAsString(), Map.class);
                return e.getStatusCode().toString() + "\n" + errorMap.getOrDefault("error", "Error desconocido") + "\n\n";
            } catch (Exception e2) {
                System.out.println(e2.getMessage());
                user.setMenu(new UserMenu(user));
                return "Error desconocido en el servidor.\n";
            }
        }catch (ResourceAccessException e) {
            System.out.println("Servidor no disponible: " + e.getMessage());
            user.setMenu(new UserMenu(user));
            return "Error: el servidor no está disponible. Intente más tarde.";
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            user.setMenu(new UserMenu(user));
            return "Error interno en el bot. \n";
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
