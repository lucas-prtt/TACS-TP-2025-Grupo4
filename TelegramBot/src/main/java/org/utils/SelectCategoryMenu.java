package org.utils;

import org.eventServerClient.dtos.event.CategoryDTO;
import org.glassfish.jersey.internal.util.Producer;
import org.menus.MenuState;
import org.users.TelegramUser;

import java.util.List;
import java.util.function.Consumer;

public class SelectCategoryMenu extends AbstractBrowseMenu<CategoryDTO>{
    private final Producer<MenuState> onBack;
    private final Consumer<CategoryDTO> onSelection;
    public SelectCategoryMenu(TelegramUser user, Consumer<CategoryDTO> onSelection, Producer<MenuState> onBack) {
        super(user);
        this.onSelection = onSelection;
        this.onBack = onBack;
    }

    @Override
    protected List fetchItems(int page, int limit) {
        return List.of(); // TODO: Llamar API get categorias
    }

    @Override
    protected String toShortString(CategoryDTO item) {
        return item.toShortString();
    }

    @Override
    protected void onItemSelected(CategoryDTO item) {
        onSelection.accept(item);
    }


    @Override
    protected MenuState getBackMenu() {
        return onBack.call();
    }
}
