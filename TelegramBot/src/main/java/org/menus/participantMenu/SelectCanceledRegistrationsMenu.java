package org.menus.participantMenu;

import org.eventServerClient.dtos.RegistrationDTO;

import java.util.List;

public class SelectCanceledRegistrationsMenu extends AbstractRegistrationBrowseMenu {
    @Override
    protected List<RegistrationDTO> fetchItems(int page, int limit) {
        return user.getApiClient().getCanceledRegistrations(page, limit);
    }

    public SelectCanceledRegistrationsMenu() {
        super();
    }
}
