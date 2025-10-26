package org.menus.adminMenu;

import org.menus.MainMenu;
import org.menus.MenuState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.utils.InlineMenuBuilder;
import org.utils.categorySelectionMenus.DeleteCategoryMenu;
import org.utils.categorySelectionMenus.SelectCategoryMenu;

public class CategoryConfigMenu extends MenuState {
    public CategoryConfigMenu() {
        super();
    }

    @Override
    public String respondTo(String message) {
        switch (message){
            case "/createCategory":
                user.setMenu(new CreateCategoryMenu());
                return null;
            case "/deleteCategory":
                user.setMenu(new DeleteCategoryMenu());
                return null;
            case "/back":
                user.setMenu(new AdminMenu());
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
