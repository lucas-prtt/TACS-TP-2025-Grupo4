package org.menus.browseMenu;

import org.eventServerClient.ApiClient;
import org.eventServerClient.dtos.event.EventDTO;
import org.menus.MenuState;
import org.users.TelegramUser;
import org.utils.AbstractBrowseMenu;

import java.util.List;

public class BrowseEventsMenu extends AbstractBrowseMenu<EventDTO> {

    public BrowseEventsMenu() {
        super();
    }

    @Override
    protected List<EventDTO> fetchItems(int page, int limit) {
        return user.getApiClient().getEventsByFilters(user.getAllFiltersAsQueryParams(), page, limit);
    }

    @Override
    protected String toShortString(EventDTO event) {
        return event.asShortString(user);
    }

    @Override
    protected void onItemSelected(EventDTO item) {
        user.setMenu(new CheckEventMenu(item));
    }

    @Override
    protected MenuState getBackMenu() {
        return new BrowseMenu();
    }
}