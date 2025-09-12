package org.menus.participantMenu;

import org.eventServerClient.ApiClient;
import org.eventServerClient.dtos.RegistrationDTO;
import org.menus.MenuState;
import org.users.TelegramUser;
import org.utils.AbstractBrowseMenu;

import java.util.List;
import java.util.UUID;

public class SelectAllRegistrationsMenu extends AbstractRegistrationBrowseMenu {
    @Override
    protected List<RegistrationDTO> fetchItems(int page, int limit) {
        return ApiClient.getAllRegistrations(UUID.fromString(user.getServerAccountId()), page, limit);
    }

    public SelectAllRegistrationsMenu(TelegramUser user) {
        super(user);
    }
}
