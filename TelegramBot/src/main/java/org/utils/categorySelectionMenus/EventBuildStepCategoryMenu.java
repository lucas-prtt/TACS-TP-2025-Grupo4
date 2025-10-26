package org.utils.categorySelectionMenus;

import lombok.Getter;
import org.eventServerClient.dtos.event.CategoryDTO;
import org.menus.MenuState;

public class EventBuildStepCategoryMenu extends SelectCategoryMenu{
    @Getter
    boolean finished = false;
    @Getter
    String category = "";

    @Override
    protected MenuState onBack() {
        return null;
    }

    @Override
    protected void onSelection(CategoryDTO selected) {
        this.category = selected.getTitle();
        this.finished = true;
    }
}
