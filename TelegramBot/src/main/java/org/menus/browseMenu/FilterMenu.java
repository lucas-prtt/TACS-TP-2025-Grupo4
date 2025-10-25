package org.menus.browseMenu;

import org.menus.MenuState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.QueryFilter;
import org.users.TelegramUser;
import org.utils.InlineMenuBuilder;
import org.utils.SelectCategoryMenu;

public class FilterMenu extends MenuState {
    public FilterMenu(TelegramUser user) {
        super(user);
    }

    @Override
    public String respondTo(String message) {
        switch (message){
            case "/filterByCategory":
                user.setMenu(new SelectCategoryMenu(user, (categoryDTO) -> {
                    user.addFilter(new QueryFilter("category", categoryDTO.getTitle()));
                    user.setMenu(new FilterMenu(user));
                },
                () -> new FilterMenu(user)));
                return null;
            case "/filterByTags":
                user.setMenu(new FilterByMenu(user, "tags"));
                return null;
            case "/filterByDate":
                user.setMenu(new FilterByDateMenu(user, "maxDate"));
                return null;
            case "/filterByTitle":
                user.setMenu(new FilterByMenu(user, "title"));
                return null;
            case "/filterByTitleContains":
                user.setMenu(new FilterByMenu(user, "titleContains"));
                return null;
            case "/filterByMinPrice":
                user.setMenu(new FilterByMenu(user, "minPrice"));
                return null;
            case "/filterByMaxPrice":
                user.setMenu(new FilterByMenu(user, "maxPrice"));
                return null;
            case "/back":
                user.setMenu(new BrowseMenu(user));
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
        return InlineMenuBuilder.localizedVerticalMenu(user, getQuestion(),"/filterByTitleContains", "/filterByTitle", "/filterByCategory",  "/filterByTags", "/filterByDate", "/filterByMinPrice", "/filterByMaxPrice", "/back");
    }
}
