package org.menus.participantMenu;

import org.menus.userMenu.UserMenu;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.menus.MenuState;
import org.utils.InlineMenuBuilder;

import java.util.List;
import java.util.Map;

public class ParticipantMenu extends MenuState {
    @Override
    public String respondTo(String message) {
        switch (message){
            case "/getSuccesfulRegistrations":
                user.setMenu(new SelectSuccesfulRegistrationsMenu(user));
                return null;
            case "/getWaitlistedRegistrations":
                user.setMenu(new SelectWaitlistedRegistrationsMenu(user));
                return null;
            case "/getCanceledRegistrations":
                user.setMenu(new SelectCanceledRegistrationsMenu(user));
                return null;
            case "/getAllRegistrations":
                user.setMenu(new SelectAllRegistrationsMenu(user));
                return null;
            default:
                return "Error - opcion invalida\n";
        }
    }

    @Override
    public String getQuestion() {
        return user.getLocalizedMessage("participantMenuQuestion",
                user.getLocalizedMessage("/getSuccesfulRegistrations"),
                user.getLocalizedMessage("/getWaitlistedRegistrations"),
                user.getLocalizedMessage("/getCanceledRegistrations"),
                user.getLocalizedMessage("/getAllRegistrations"),
                user.getLocalizedMessage("/start")
        );
    }

    public ParticipantMenu(TelegramUser user) {
        super(user);
    }
    @Override
    public SendMessage questionMessage() {
        SendMessage message = InlineMenuBuilder.localizedVerticalMenu(user, getQuestion(), "/getSuccesfulRegistrations", "/getWaitlistedRegistrations", "/getCanceledRegistrations", "/getAllRegistrations", "/start");
        return message;
    }
}
