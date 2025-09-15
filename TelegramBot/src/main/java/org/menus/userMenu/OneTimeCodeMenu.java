package org.menus.userMenu;
import org.eventServerClient.ApiClient;
import org.eventServerClient.dtos.AccountDTO;
import org.menus.MainMenu;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.menus.MenuState;

import java.util.Map;

public class OneTimeCodeMenu extends MenuState {

    public OneTimeCodeMenu(TelegramUser user) {
        super(user);
    }

    @Override
    public String respondTo(String message) {
        try {
            Map<String, Object> response = user.getApiClient().loginOneTimeCode(message);
            user.updateUser(response);
            user.setMenu(new MainMenu(user));
            return "Cuenta establecida:\n" +
                    "  Usuario: "+ user.getServerAccountUsername() +
                    "\n  Uuid: " + user.getServerAccountId()  + "\n\n";
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return "Error al asignar el usuario. Vuelva a intentar o escriba /start para volver al inicio\n\n   >" + e.getMessage();

        }
    }
    @Override
    public SendMessage questionMessage() {
        SendMessage message = sendMessageText(getQuestion());
        return message;
    }

    @Override
    public String getQuestion() {
        return "Ingrese el one Time Code";
    }
}
