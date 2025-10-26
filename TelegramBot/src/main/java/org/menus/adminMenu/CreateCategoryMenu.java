package org.menus.adminMenu;

import org.eventServerClient.dtos.event.CategoryDTO;
import org.menus.MainMenu;
import org.menus.MenuState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.utils.InlineMenuBuilder;

import java.util.Objects;

public class CreateCategoryMenu extends MenuState {
    @Override
    public String respondTo(String message) {
        if(Objects.equals(message, "/back")){
            user.setMenu(new CategoryConfigMenu());
            return null;
        }
        user.getApiClient().postCategory(new CategoryDTO(message));
        user.setMenu(new MainMenu());
        return null;
    }

    @Override
    public String getQuestion() {
        return user.getLocalizedMessage("requestInputNewCategory");
    }

    @Override
    public SendMessage questionMessage() {
        return InlineMenuBuilder.localizedVerticalMenu(user, getQuestion(), "/back");
    }

    public CreateCategoryMenu() {
        super();
    }
}
