package org.utils.categorySelectionMenus;

import org.eventServerClient.dtos.event.CategoryDTO;
import org.menus.MainMenu;
import org.menus.MenuState;
import org.menus.adminMenu.AdminMenu;

import java.awt.*;

public class DeleteCategoryMenu extends SelectCategoryMenu{
    @Override
    protected MenuState onBack() {
        return new AdminMenu();
    }

    @Override
    protected void onSelection(CategoryDTO selected) {
        user.getApiClient().deleteCategory(selected);
        user.setMenu(new MainMenu());
    }
}
