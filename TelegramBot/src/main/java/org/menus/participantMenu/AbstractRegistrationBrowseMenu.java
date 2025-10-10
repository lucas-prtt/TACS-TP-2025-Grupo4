package org.menus.participantMenu;

import org.eventServerClient.dtos.RegistrationDTO;
import org.menus.MenuState;
import org.users.TelegramUser;
import org.utils.AbstractBrowseMenu;

import java.util.List;

public abstract class AbstractRegistrationBrowseMenu extends AbstractBrowseMenu<RegistrationDTO> {

    public AbstractRegistrationBrowseMenu(TelegramUser user) {
        super(user);
    }

    @Override
    protected String toShortString(RegistrationDTO item) {
        return item.toShortString(user);
    }

    @Override
    protected MenuState itemSelectedMenu(RegistrationDTO item) {
        return new CheckRegistrationMenu(user, item);
    }

    @Override
    protected MenuState getBackMenu() {
        return new ParticipantMenu(user);
    }
}
