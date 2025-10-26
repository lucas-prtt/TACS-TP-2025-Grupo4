package org.utils.categorySelectionMenus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eventServerClient.dtos.event.CategoryDTO;
import org.menus.MenuState;
@NoArgsConstructor
@Setter
@Getter
public class EventBuildStepCategoryMenu extends SelectCategoryMenu{
    boolean finished = false;
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
