package org.menus.userMenu;

import org.eventServerClient.ApiClient;
import org.eventServerClient.dtos.AccountDTO;
import org.users.TelegramUser;
import org.menus.MenuState;

public class SetUserMenu extends MenuState {

    public SetUserMenu(TelegramUser user) {
        super(user);
    }

    @Override
    public String respondTo(String message) {
        try {
            AccountDTO acc = ApiClient.getAccountByUsername(message);
            user.setServerAccountUsername(acc.getUsername());
            user.setServerAccountId(acc.getUuid());
            return "Cuenta establecida:\n" +
                    "  Usuario: "+ acc.getUsername() +
                    "\n  Uuid: " + acc.getUuid()  + "\n"
                    + user.setMainMenuAndRespond();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return "Error al asignar el usuario. Vuelva a intentar o escriba /start para volver al inicio\n\n   >" + e.getMessage();

        }
    }

    @Override
    public String getQuestion() {
        return "Ingrese el username";
    }
}
