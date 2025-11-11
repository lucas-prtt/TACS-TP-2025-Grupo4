package org.menus.participantMenu;

import org.eventServerClient.dtos.RegistrationDTO;

import java.util.List;

public class SelectAllRegistrationsMenu extends AbstractRegistrationBrowseMenu {
    @Override
    protected List<RegistrationDTO> fetchItems(int page, int limit) {
        return user.getApiClient().getAllRegistrations(page, limit);
    }

    public SelectAllRegistrationsMenu() {
        super();
    }
}
