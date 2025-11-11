package org.menus.browseMenu;

import org.menus.MenuState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.utils.InlineMenuBuilder;
import org.utils.categorySelectionMenus.AddFilterCategoryMenu;

public class FilterMenu extends MenuState {
    public FilterMenu() {
        super();
    }

    @Override
    public String respondTo(String message) {
        switch (message){
            case "/filterByCategory":
                user.setMenu(new AddFilterCategoryMenu());
                return null;
            case "/filterByTags":
                user.setMenu(new FilterByMenu( "tags"));
                return null;
            case "/filterByDate":
                user.setMenu(new FilterByDateMenu( "maxDate"));
                return null;
            case "/filterByTitle":
                user.setMenu(new FilterByMenu( "title"));
                return null;
            case "/filterByTitleContains":
                user.setMenu(new FilterByMenu( "titleContains"));
                return null;
            case "/filterByMinPrice":
                user.setMenu(new FilterByMenu( "minPrice"));
                return null;
            case "/filterByMaxPrice":
                user.setMenu(new FilterByMenu( "maxPrice"));
                return null;
            case "/showFilters":
                return user.getLocalizedMessage("filters:") + " \n" + String.join("\n   ",user.getFiltros().stream().map(queryFilter -> queryFilter.toLocalizedString(user)).toList());
            case "/back":
                user.setMenu(new BrowseMenu());
                return null;
            default:
                return user.getLocalizedMessage("wrongOption");
        }
    }

    @Override
    public String getQuestion() {
        return user.getLocalizedMessage("filterMenuQuestion");
    }

    @Override
    public SendMessage questionMessage() {
        return InlineMenuBuilder.localizedVerticalMenu(user, getQuestion(),"/filterByTitleContains", "/filterByTitle", "/filterByCategory",  "/filterByTags", "/filterByDate", "/filterByMinPrice", "/filterByMaxPrice", "/showFilters", "/back");
    }
}
