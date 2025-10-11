package org.menus.organizerMenu;

import org.eventServerClient.ApiClient;
import org.eventServerClient.dtos.event.EventDTO;
import org.menus.MenuState;
import org.users.TelegramUser;
import org.utils.AbstractBrowseMenu;

import java.util.List;
import java.util.UUID;

public class ManageEventSelectionMenu extends AbstractBrowseMenu<EventDTO>{

    public ManageEventSelectionMenu(TelegramUser user) {
        super(user);
    }

    @Override
    protected List<EventDTO> fetchItems(int page, int limit) {
        return user.getApiClient().getEventsOrganizedBy(page, limit);
    }

    @Override
    protected String toShortString(EventDTO item) {
        return item.asShortString(user);
    }

    @Override
    protected MenuState itemSelectedMenu(EventDTO item) {
        return new ManageEventMenu(user, item);
    }

    @Override
    protected MenuState getBackMenu() {
        return new OrganizerMenu(user);
    }
}
