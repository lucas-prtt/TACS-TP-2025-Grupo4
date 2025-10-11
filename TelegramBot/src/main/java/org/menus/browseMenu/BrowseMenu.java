package org.menus.browseMenu;

import org.menus.MenuState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.QueryFilter;
import org.users.TelegramUser;
import org.utils.InlineMenuBuilder;

import java.util.List;

public class BrowseMenu extends MenuState {
    @Override
    public String respondTo(String message) {
        switch (message){
            case "/browse":
                user.setMenu(new BrowseEventsMenu(user));
                return null;
            case "/filter":
                user.setMenu(new FilterMenu(user));
                return null;
            case "/showFilters":
                return "Filtros: \n" + String.join("\n   ",user.getFiltros().stream().map(queryFilter -> queryFilter.toLocalizedString(user)).toList());
            case "/clearFilters":
                user.clearFilters();
                return user.getLocalizedMessage("filtersCleared");
            case "/lookupUUID":
                user.setMenu(new LookUpEventByUUIDMenu(user));
                return null;
            default:
                return user.getLocalizedMessage("wrongOption");
        }
    }

    @Override
    public String getQuestion() {
        return
               user.getLocalizedMessage("browseMenuQuestion") + "\n" +
                user.getLocalizedMessage("appliedFilters") + " " +
                        (user.getFiltros().isEmpty() ? user.getLocalizedMessage("noFilters") :
                        "\n" +
                String.join("\n", user.getFiltros().stream().map(queryFilter -> queryFilter.toLocalizedString(user)).toList()));
    }

    @Override
    public SendMessage questionMessage() {
        SendMessage message = InlineMenuBuilder.localizedVerticalMenu(user, getQuestion(), "/browse", "/lookupUUID", "/filter", "/showFilters","/clearFilters", "/start");
        return message;
    }

    public BrowseMenu(TelegramUser user) {
        super(user);
    }
}
