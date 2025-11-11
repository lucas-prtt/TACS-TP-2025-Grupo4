package org.menus.participantMenu;

import org.eventServerClient.dtos.RegistrationDTO;
import org.menus.MenuState;
import org.utils.AbstractBrowseMenu;


public abstract class AbstractRegistrationBrowseMenu extends AbstractBrowseMenu<RegistrationDTO> {

    public AbstractRegistrationBrowseMenu() {
        super();
    }

    @Override
    protected String toShortString(RegistrationDTO item) {
        return item.toShortString(user);
    }

    @Override
    protected void onItemSelected(RegistrationDTO item) {
        user.setMenu(new CheckRegistrationMenu(item));
    }

    @Override
    protected MenuState getBackMenu() {
        return new ParticipantMenu();
    }
}
