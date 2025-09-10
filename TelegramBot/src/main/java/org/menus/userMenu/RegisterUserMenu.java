package org.menus.userMenu;

import org.eventServerClient.ApiClient;
import org.eventServerClient.dtos.PostUsuarioDTO;
import org.users.TelegramUser;
import org.menus.MenuState;

public class RegisterUserMenu extends MenuState {
    @Override
    // Recibe el nombre del usuario y lo intenta crear. Si no puede devuelve un error. Si lo crea lo establece como el usado
    public String respondTo(String nombreDeUsuario) {
        try {
            PostUsuarioDTO usuarioCreado = ApiClient.postUsuario(nombreDeUsuario);
            user.setServerAccountId(usuarioCreado.getUuid());
            user.setServerAccountUsername(usuarioCreado.getUsername());
            return "Cuenta creada y establecida como la actual\n" +
                    "\tUsuario: "+ usuarioCreado.getUsername() +
                    "\n\tUuid:" + usuarioCreado.getUuid()  + "\n"
                    + user.setMainMenuAndRespond();
        }catch (Exception e){
            System.out.println(e.getMessage());
            return "Error al crear usuario. Vuelva a intentar o escriba /start para volver al inicio\n\n   >" + e.getMessage();
        }
    }

    @Override
    public String getQuestion() {
        return "Ingrese el nombre del usuario a crear";
    }

    public RegisterUserMenu(TelegramUser user) {
        super(user);
    }
}
