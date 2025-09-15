package org.menus.participantMenu;

import org.menus.userMenu.UserMenu;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.menus.MenuState;
import org.utils.InlineMenuBuilder;

import java.util.List;

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
    @Override
    public SendMessage questionMessage() {
        SendMessage message = InlineMenuBuilder.menu(getQuestion(), List.of("/getSuccesfulRegistrations", "/getWaitlistedRegistrations", "/getCanceledRegistrations"), List.of("/getAllRegistrations", "/start"));
        return message;
    }
}
