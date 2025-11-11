package org.menus.participantMenu;

import org.eventServerClient.dtos.RegistrationDTO;

import java.util.List;

public class SelectWaitlistedRegistrationsMenu extends AbstractRegistrationBrowseMenu {
    @Override
    protected List<RegistrationDTO> fetchItems(int page, int limit) {
        return user.getApiClient().getWaitlistRegistrations(page, limit);
    }

    public SelectWaitlistedRegistrationsMenu() {
        super();
    }
}
