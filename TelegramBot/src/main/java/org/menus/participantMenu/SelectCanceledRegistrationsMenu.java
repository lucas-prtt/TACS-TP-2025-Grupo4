package org.menus.participantMenu;

import org.eventServerClient.ApiClient;
import org.eventServerClient.dtos.RegistrationDTO;
import org.menus.MenuState;
import org.users.TelegramUser;
import org.utils.AbstractBrowseMenu;

import java.util.List;
import java.util.UUID;

public class SelectCanceledRegistrationsMenu extends AbstractRegistrationBrowseMenu {
    @Override
    protected List<RegistrationDTO> fetchItems(int page, int limit) {
        return ApiClient.getCanceledRegistrations(UUID.fromString(user.getServerAccountId()), page, limit);
    }

    public SelectCanceledRegistrationsMenu(TelegramUser user) {
        super(user);
    }
}
