package org.menus.participantMenu;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.menus.MenuState;
import org.utils.InlineMenuBuilder;

public class ParticipantMenu extends MenuState {
    @Override
    public String respondTo(String message) {
        switch (message){
            case "/getSuccesfulRegistrations":
                user.setMenu(new SelectSuccesfulRegistrationsMenu());
                return null;
            case "/getWaitlistedRegistrations":
                user.setMenu(new SelectWaitlistedRegistrationsMenu());
                return null;
            case "/getCanceledRegistrations":
                user.setMenu(new SelectCanceledRegistrationsMenu());
                return null;
            case "/getAllRegistrations":
                user.setMenu(new SelectAllRegistrationsMenu());
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

    public ParticipantMenu() {
        super();
    }
    @Override
    public SendMessage questionMessage() {
        SendMessage message = InlineMenuBuilder.localizedVerticalMenu(user, getQuestion(), "/getSuccesfulRegistrations", "/getWaitlistedRegistrations", "/getCanceledRegistrations", "/getAllRegistrations", "/start");
        return message;
    }
}
