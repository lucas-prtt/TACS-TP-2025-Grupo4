package org.menus.browseMenu;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.menus.MenuState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.QueryFilter;
import org.utils.inputSteps.GenericDateTimeInputStep;
@Getter
@Setter
@NoArgsConstructor
public class FilterByDateMenu extends MenuState {
    QueryFilter filter;
    GenericDateTimeInputStep input;
    public FilterByDateMenu(String filterParameter) {
        super();
        this.filter = new QueryFilter(filterParameter);
        input = new GenericDateTimeInputStep("value");
    }


    @Override
    public String respondTo(String message) {
        if(!input.handleInput(message, filter, user))
            return null;
        user.addFilter(filter);
        user.setMenu(new FilterMenu());
        return user.getLocalizedMessage("configuredFilter", filter.getTypeLocalized(user), filter.getValue());
    }

    @Override
    public String getQuestion() {
        return " - ";
    }
    @Override
    public SendMessage questionMessage() {
        return input.getQuestion(user);
    }
}
