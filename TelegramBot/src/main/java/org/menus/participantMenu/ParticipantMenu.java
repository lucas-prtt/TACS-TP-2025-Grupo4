package org.menus.participantMenu;

import org.users.TelegramUser;
import org.menus.MenuState;

public class ParticipantMenu extends MenuState {
    @Override
    public String respondTo(String message) {
        switch (message){
            case "/getSuccesfulRegistrations":
                return user.setMenuAndRespond(new SelectSuccesfulRegistrationsMenu(user));
            case "/getWaitlistedRegistrations":
                return user.setMenuAndRespond(new SelectWaitlistedRegistrationsMenu(user));
            case "/getCanceledRegistrations":
                return user.setMenuAndRespond(new SelectCanceledRegistrationsMenu(user));
            case "/getAllRegistrations":
                return user.setMenuAndRespond(new SelectAllRegistrationsMenu(user));
            default:
                return "Error - opcion invalida\n" + user.getMenu().getQuestion();
        }
    }

    @Override
    public String getQuestion() {
        return """
                Menu de participante:
                
                /getSuccesfulRegistrations
                    - Ver registros a eventos como participante
                /getWaitlistedRegistrations
                    - Ver registros a eventos en la waitlist
                /getCanceledRegistrations
                    - Ver registros cancelados
                /getAllRegistrations
                    - Ver todos los registros
                /start
                    - Volver al menú principal
                """;
    }

    public ParticipantMenu(TelegramUser user) {
        super(user);
    }
}
