package org;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.users.TelegramUser;
import org.users.TelegramUserRepository;

import java.util.List;
import java.util.Optional;

public class BotEventos extends TelegramLongPollingBot {

    private final String BOT_USERNAME;
    private final TelegramUserRepository telegramUserRepository = new TelegramUserRepository();

    public BotEventos(String token, String botUsername) {
        super(token);
        BOT_USERNAME = botUsername;
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            SendMessage response;


            //Busca el usuario en el repo
            Optional<TelegramUser> senderOpt = telegramUserRepository.getUser(chatId);
            //Si es nuevo, dice bienvenido y muestra el menu principal
            if(senderOpt.isEmpty()){
                TelegramUser user = telegramUserRepository.addUser(chatId, new TelegramUser(chatId));
                response = new SendMessage();
                response.setChatId(chatId);
                response.setText("Bienvenido! \n" + user.getMenu().getQuestion() );
            }
            //De lo contrario, muestra respuesta al ultimo menu
            else {
                response = senderOpt.get().respondTo(update.getMessage());
            }


            if(response.getText() == null)
                response.setText("Error: Null message");
            try {
                execute(response); // Envía el mensaje al usuario
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }
}
