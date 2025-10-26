package org.menus.adminMenu;

import org.eventServerClient.ApiClient;
import org.menus.MainMenu;
import org.menus.MenuState;
import org.menus.userMenu.UserMenu;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.utils.InlineMenuBuilder;
import org.utils.SelectCategoryMenu;

public class CategoryConfigMenu extends MenuState {
    public CategoryConfigMenu(TelegramUser user) {
        super(user);
    }

    @Override
    public String respondTo(String message) {
        switch (message){
            case "/createCategory":
                user.setMenu(new CreateCategoryMenu(user));
                return null;
            case "/deleteCategory":
                user.setMenu(new SelectCategoryMenu(user, (categoryDTO) -> {
                    user.getApiClient().deleteCategory(categoryDTO);
                    user.setMenu(new MainMenu(user));
                },
                        () -> this
                ));
                return null;
            case "/back":
                user.setMenu(new AdminMenu(user));
                return null;
            default:
                return user.getLocalizedMessage("wrongOption");
        }
    }

    @Override
    public String getQuestion() {
        return user.getLocalizedMessage("CategoryConfigMenuQuestion", user.getLocalizedMessage("/deleteCategory"), user.getLocalizedMessage("/createCategory"));
    }

    @Override
    public SendMessage questionMessage() {
        return InlineMenuBuilder.localizedVerticalMenu(user, getQuestion(), "/createCategory", "/deleteCategory", "/back", "/start");
    }
}
