import org.eventServerClient.ApiClient;
import org.eventServerClient.dtos.AccountDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.users.TelegramUser;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TelegramMenuTest {
    TelegramUser user;

    public Update getMockUpdate(boolean hasCallBackQuery, String message){
        Update mockUpdate = Mockito.mock(Update.class);
        when(mockUpdate.hasCallbackQuery()).thenReturn(hasCallBackQuery);
        when(mockUpdate.hasMessage()).thenReturn(!hasCallBackQuery);
        if(hasCallBackQuery){
            when(mockUpdate.getCallbackQuery()).thenReturn(Mockito.mock(CallbackQuery.class));
            when(mockUpdate.getCallbackQuery().getData()).thenReturn(message);
        }else {
            when(mockUpdate.getMessage()).thenReturn(Mockito.mock(Message.class));
            when(mockUpdate.getMessage().getText()).thenReturn(message);
        }
        return mockUpdate;
    }

    @BeforeEach
    void setUp() {
        user = new TelegramUser(123456789L);
        ApiClient mockClient = mock(ApiClient.class);
        AccountDTO registerResponse = new AccountDTO("Pepe", null);
        registerResponse.setUuid("f6834356-d03d-4450-8df1-9644caf9fe7d");
        when(mockClient.postAccount("Pepe", "Pepe123##")).thenReturn(registerResponse);
        when(mockClient.loginUserAndPassword(new AccountDTO("Pepe","Pepe123##"))).thenReturn(Map.of("roles", List.of("USER"), "username" , "Pepe", "token", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJQZXBlIiwiYWNjb3VudElkIjoiZjY4MzQzNTYtZDAzZC00NDUwLThkZjEtOTY0NGNhZjlmZTdkIiwicm9sZXMiOlsiVVNFUiJdLCJpYXQiOjE3NTgwMjM1NDQsImV4cCI6MTc1ODYyODM0NH0.igUDNFWCJ8VJ2VA0lbVuDjvNL9gRcki3J6JI5d7Xr2o", "id", "f6834356-d03d-4450-8df1-9644caf9fe7d"));
        when(mockClient.loginOneTimeCode("Pepe", "Pepe123##")).thenReturn(Map.of("roles", List.of("USER"), "username" , "Pepe", "token", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJQZXBlIiwiYWNjb3VudElkIjoiZjY4MzQzNTYtZDAzZC00NDUwLThkZjEtOTY0NGNhZjlmZTdkIiwicm9sZXMiOlsiVVNFUiJdLCJpYXQiOjE3NTgwMjM1NDQsImV4cCI6MTc1ODYyODM0NH0.igUDNFWCJ8VJ2VA0lbVuDjvNL9gRcki3J6JI5d7Xr2p", "id", "f6834356-d03d-4450-8df1-9644caf9fe7d"));
        user.setApiClient(mockClient);
    }
    @Test
    void menuPrincipal(){
        Update update1 = getMockUpdate(false, "/start");
        SendMessage response1 = user.respondTo(update1);
        SendMessage question1 = user.getQuestion();
        assert (response1 == null);
        assert (question1.getText().startsWith("Menu principal"));
        assert (question1.getReplyMarkup() != null);


        Update update2 = getMockUpdate(false, "/buenosDias");
        SendMessage response2 = user.respondTo(update2);
        SendMessage question2 = user.getQuestion();
        assert (response2.getText().equals("Error - opcion invalida\n"));
        assert (question2.getText().startsWith("Menu principal"));
        assert (question2.getReplyMarkup() != null);


        Update update3 = getMockUpdate(true, "/buenosDias");
        SendMessage response3 = user.respondTo(update3);
        SendMessage question3 = user.getQuestion();
        assert (response3.getText().equals("Error - opcion invalida\n"));
        assert (question3.getText().startsWith("Menu principal"));
        assert (question3.getReplyMarkup() != null);


        Update update4 = getMockUpdate(true, "/organizerMenu");
        SendMessage response4 = user.respondTo(update4);
        SendMessage question4 = user.getQuestion();
        assert (response4.getText().equals("Inicia sesion antes de entrar a estos menus"));
        assert (question4.getText().startsWith("Menu principal"));
        assert (question4.getReplyMarkup() != null);

        Update update5 = getMockUpdate(true, "/browseMenu");
        SendMessage response5 = user.respondTo(update5);
        SendMessage question5 = user.getQuestion();
        assert (response5.getText().equals("Inicia sesion antes de entrar a estos menus"));
        assert (question5.getText().startsWith("Menu principal"));
        assert (question5.getReplyMarkup() != null);

        Update update6 = getMockUpdate(true, "/participantMenu");
        SendMessage response6 = user.respondTo(update6);
        SendMessage question6 = user.getQuestion();
        assert (response6.getText().equals("Inicia sesion antes de entrar a estos menus"));
        assert (question6.getText().startsWith("Menu principal"));
        assert (question6.getReplyMarkup() != null);


        Update update7 = getMockUpdate(true, "/userMenu");
        SendMessage response7 = user.respondTo(update7);
        SendMessage question7 = user.getQuestion();
        assert (response7 == null);
        assert (question7.getText().contains("Menu de Usuario"));
        assert (question7.getReplyMarkup() != null);

    }

    @Test
    void menuDeUsuario(){
        Update update1 = getMockUpdate(false, "/start");
        SendMessage response1 = user.respondTo(update1);
        SendMessage question1 = user.getQuestion();
        assert (response1 == null);
        assert (question1.getText().startsWith("Menu principal"));
        assert (question1.getReplyMarkup() != null);


        Update update2 = getMockUpdate(false, "/userMenu");
        SendMessage response2 = user.respondTo(update2);
        SendMessage question2 = user.getQuestion();
        assert (response2 == null);
        assert (question2.getText().contains("Menu de Usuario"));
        assert (question2.getReplyMarkup() != null);


        Update update3 = getMockUpdate(true, "/registerUser");
        SendMessage response3 = user.respondTo(update3);
        SendMessage question3 = user.getQuestion();
        assert (response3 == null);
        assert (question3.getText().startsWith("Ingrese el nombre del usuario a crear"));
        assert (question3.getReplyMarkup() == null);


        Update update4 = getMockUpdate(false, "Pepe");
        SendMessage response4 = user.respondTo(update4);
        SendMessage question4 = user.getQuestion();
        assert (response4 == null);
        assert (question4.getText().startsWith("Ingrese la contraseña"));
        assert (question4.getReplyMarkup() == null);


        Update update5 = getMockUpdate(false, "Pepe123##");
        SendMessage response5 = user.respondTo(update5);
        SendMessage question5 = user.getQuestion();
        assert (response5.getText().equals("Cuenta creada\n" + "userID: " + "f6834356-d03d-4450-8df1-9644caf9fe7d" + "\n"));
        assert (question5.getText().contains("Menu principal"));
        assert (question5.getReplyMarkup() != null);


        Update updatea = getMockUpdate(false, "/userMenu");
        SendMessage responsea = user.respondTo(updatea);
        SendMessage questiona = user.getQuestion();
        assert (responsea == null);
        assert (questiona.getText().contains("Menu de Usuario"));
        assert (questiona.getReplyMarkup() != null);


        Update update6 = getMockUpdate(false, "/loginUser");
        SendMessage response6 = user.respondTo(update6);
        SendMessage question6 = user.getQuestion();
        assert (response6 == null);
        assert (question6.getText().startsWith("Ingrese el nombre del usuario"));
        assert (question6.getReplyMarkup() == null);


        Update update7 = getMockUpdate(false, "Pepe");
        SendMessage response7 = user.respondTo(update7);
        SendMessage question7 = user.getQuestion();
        assert (response7 == null);
        assert (question7.getText().startsWith("Ingrese la contraseña"));
        assert (question7.getReplyMarkup() == null);

        Update update8 = getMockUpdate(false, "Pepe123##");
        SendMessage response8 = user.respondTo(update8);
        SendMessage question8 = user.getQuestion();
        assert (response8.getText().equals("Cuenta Logueada\n" + "userID: " + "f6834356-d03d-4450-8df1-9644caf9fe7d" + "\n" + "username: " + "Pepe" + "\n"));
        assert (question8.getText().startsWith("Menu principal"));
        assert (question8.getReplyMarkup() != null);
    }

}
