package org.menus.browseMenu;

import org.menus.MenuState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.QueryFilter;
import org.users.TelegramUser;
import org.utils.InlineMenuBuilder;

import java.util.List;

public class FilterByMenu extends MenuState {
    QueryFilter filter;

    public FilterByMenu(TelegramUser user, String filterParameter) {
        super(user);
        this.filter = new QueryFilter(filterParameter);
    }


    @Override
    public String respondTo(String message) {
        filter.setValue(message);
        user.addFilter(filter);
        user.setMenu(new FilterMenu(user));
        return user.getLocalizedMessage("configuredFilter", filter.getTypeLocalized(user), message);
    }

    @Override
    public String getQuestion() {
        return user.getLocalizedMessage("requestInputFilter", filter.getTypeLocalized(user));
    }
    @Override
    public SendMessage questionMessage() {
        return sendMessageText(getQuestion());
    }
}
