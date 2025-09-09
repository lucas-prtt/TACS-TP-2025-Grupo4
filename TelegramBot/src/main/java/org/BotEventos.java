package org;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class BotEventos extends TelegramLongPollingBot {

    private final String BOT_USERNAME;

    public BotEventos(String token, String botUsername) {
        super(token);
        BOT_USERNAME = botUsername;

    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();
            SendMessage response = new SendMessage();
            response.setChatId(chatId);
            switch (messageText){
                case "/start":
                    response.setText("Bot iniciado");
                    break;
                case "/estado":
                    response.setText("Funcionando");
                    break;
                default:
                    response.setText("Comando desconocido");
                    break;
            }

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
