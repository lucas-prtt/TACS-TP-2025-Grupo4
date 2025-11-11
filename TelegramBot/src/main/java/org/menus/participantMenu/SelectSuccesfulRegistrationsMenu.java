package org.menus.participantMenu;

import org.eventServerClient.dtos.RegistrationDTO;

import java.util.List;

public class SelectSuccesfulRegistrationsMenu extends AbstractRegistrationBrowseMenu {
    @Override
    protected List<RegistrationDTO> fetchItems(int page, int limit) {
        return user.getApiClient().getRegisteredRegistrations(page, limit);
    }

    public SelectSuccesfulRegistrationsMenu() {
        super();
    }
}
