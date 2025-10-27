package org.menus.browseMenu;

import com.sun.tools.javac.Main;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eventServerClient.ApiClient;
import org.eventServerClient.dtos.RegistrationDTO;
import org.eventServerClient.dtos.RegistrationStateDTO;
import org.eventServerClient.dtos.event.EventDTO;
import org.menus.MainMenu;
import org.menus.MenuState;
import org.menus.userMenu.UserMenu;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.utils.ErrorHandler;
import org.utils.InlineMenuBuilder;

import java.util.List;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
public class CheckEventMenu extends MenuState {
    EventDTO evento;
    MenuState backMenu;
    public CheckEventMenu(EventDTO eventDTO, MenuState backMenu) {
        super();
        evento = eventDTO;
        this.backMenu = backMenu;
    }

    @Override
    public String respondTo(String message) {
        if(message.equals("/back")){
            user.setMenu(backMenu);
            return null;
        }
        if (message.equals("/registerUser")) {
            if (user.getServerAccountId() == null) {
                user.setMenu(new UserMenu());
                return user.getLocalizedMessage("mustLoginFirst") ;
            }
            try {
                RegistrationDTO response = user.getApiClient().postRegistration(evento.getId());

                if (response.getState().equals(RegistrationStateDTO.CONFIRMED)) {
                    user.setMenu(new MainMenu());
                    return user.getLocalizedMessage("registrationConfirmedParticipant");
                } else if (response.getState().equals(RegistrationStateDTO.WAITLIST)) {
                    user.setMenu(new MainMenu());
                    return user.getLocalizedMessage("registrationConfirmedWaitlist");
                } else {
                    user.setMenu(new MainMenu());
                    return user.getLocalizedMessage("unknownErrorInServer");
                }
            } catch (HttpClientErrorException e) {
                return ErrorHandler.getErrorMessage(e, user);
            }catch (ResourceAccessException e) {
                System.out.println("Servidor no disponible: " + e.getMessage());
                user.setMenu(new UserMenu());
                return user.getLocalizedMessage("unknownErrorInServer");
            }catch (Exception e){
                user.setMenu(new MainMenu());
                return "Error desconocido";
            }
        }
        return user.getLocalizedMessage("wrongOption");
    }

    @Override
    public String getQuestion() {
        return evento.asDetailedString(user) +
                "\n\n"+
                user.getLocalizedMessage("/registerUser") + "  -->  " + (evento.getMaxParticipants() == evento.getRegistered() ?  "Registrarse a la waitlist\n" : "Registrarse al evento\n");
    }
    @Override
    public SendMessage questionMessage() {
        SendMessage message = InlineMenuBuilder.localizedVerticalMenu(user, getQuestion(), "/registerUser", "/back", "/start");
        return message;
    }

}
