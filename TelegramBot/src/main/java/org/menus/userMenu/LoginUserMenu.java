package org.menus.userMenu;

import org.eventServerClient.dtos.AccountDTO;
import org.users.TelegramUser;
import org.menus.MenuState;

import java.util.Map;

public class LoginUserMenu extends MenuState {
    AccountDTO newUser = new AccountDTO();
    @Override
    // Recibe el nombre del usuario y lo intenta crear. Si no puede devuelve un error. Si lo crea lo establece como el usado
    public String respondTo(String message) {
        try {
            if(newUser.getUsername() == null){
                newUser.setUsername(message);
                return "Ingrese la contraseña";
            }
            newUser.setPassword(message);
            Map<String, Object> res = user.getApiClient().loginUserAndPassword(newUser);
            user.updateUser(res);
            return "Cuenta Logueada\n" + "userID: " + user.getServerAccountId() + "\n"
                    + "username: " + user.getServerAccountUsername() + "\n"
                    + user.setMainMenuAndRespond();
        }catch (Exception e){
            System.out.println(e.getMessage());
            return "Error al loguearse al usuario." + e.getMessage() + user.setMenuAndRespond(new UserMenu(user));
        }
    }

    @Override
    public String getQuestion() {
        return "Ingrese el nombre del usuario a crear";
    }

    public LoginUserMenu(TelegramUser user) {
        super(user);
    }
}
