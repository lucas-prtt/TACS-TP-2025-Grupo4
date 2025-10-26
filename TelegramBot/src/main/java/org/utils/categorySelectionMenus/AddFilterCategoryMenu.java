package org.utils.categorySelectionMenus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eventServerClient.dtos.event.CategoryDTO;
import org.menus.MenuState;
import org.menus.browseMenu.FilterMenu;
import org.users.QueryFilter;
@NoArgsConstructor
@Setter
@Getter
public class AddFilterCategoryMenu extends SelectCategoryMenu{
    @Override
    protected MenuState onBack() {
        return new FilterMenu();
    }

    @Override
    protected void onSelection(CategoryDTO selected) {
        user.addFilter(new QueryFilter("category", selected.getTitle()));
        user.setMenu(new FilterMenu());
    }
}
