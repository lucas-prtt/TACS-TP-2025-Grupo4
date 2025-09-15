package org.menus.browseMenu;

import org.menus.MenuState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.utils.InlineMenuBuilder;

import java.util.List;

public class FilterByMenu extends MenuState {

    String filterParameter;
    public FilterByMenu(TelegramUser user, String filterParameter) {
        super(user);
        this.filterParameter = filterParameter;
    }


    @Override
    public String respondTo(String message) {
        user.addFilter(filterParameter + "="+ message);
        user.setMenu(new BrowseMenu(user));
        return "Filtro configurado: " + filterParameter + "="+ message + "\n\n";
    }

    @Override
    public String getQuestion() {
        return "Ingrese el nombre para el filtro \""+ filterParameter +"\"\n" ;
    }
    @Override
    public SendMessage questionMessage() {
        return sendMessageText(getQuestion());
    }
}
