package org.menus.browseMenu;

import org.menus.MenuState;
import org.users.TelegramUser;

public class FilterByMenu extends MenuState {

    String filterParameter;
    public FilterByMenu(TelegramUser user, String filterParameter) {
        super(user);
        this.filterParameter = filterParameter;
    }


    @Override
    public String respondTo(String message) {
        user.addFilter(filterParameter + "="+ message);
        return "";
    }

    @Override
    public String getQuestion() {
        return "Ingrese el nombre para el filtro \""+ filterParameter +"\"\n" ;
    }

}
