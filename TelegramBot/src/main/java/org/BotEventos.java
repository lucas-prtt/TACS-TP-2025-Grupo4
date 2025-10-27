package org;

import lombok.Setter;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.users.CacheTelegramUserRepository;
import org.users.TelegramUser;
import org.users.TelegramUserRepository;
import org.utils.InlineMenuBuilder;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BotEventos extends TelegramLongPollingBot {

    private final String BOT_USERNAME;
    @Setter
    protected TelegramUserRepository telegramUserRepository;

    public BotEventos(String token, String botUsername) {
        super(token);
        BOT_USERNAME = botUsername;
    }



    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = -1L;
        SendMessage response = null;
        SendMessage question = null;
        TelegramUser user;
        Optional<TelegramUser> senderOpt;
        if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId();
        }else if (update.hasCallbackQuery()){
            chatId = update.getCallbackQuery().getMessage().getChatId();
        }

            //Busca el usuario en el repo
            try {
                senderOpt = telegramUserRepository.getUser(chatId);
            }catch (JedisConnectionException ex){
                SendMessage msg = InlineMenuBuilder.menu("⚠️ Our database is offline.\n\n⏳ Please wait", Map.of("\uD83D\uDD04 Retry", "/start"));
                msg.setChatId(chatId);
                try {
                    execute(msg);
                    return;
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }catch (Exception e){
                senderOpt = Optional.empty();
            }
            //Si es nuevo, dice bienvenido y muestra el menu principal
            if(senderOpt.isEmpty()){
                user = telegramUserRepository.addUser(chatId, new TelegramUser(chatId));
                question = user.getQuestion();
                question.setChatId(chatId);
            }
            //De lo contrario, muestra respuesta al ultimo menu
            else {
                user = senderOpt.get();
                response = user.respondTo(update);
                question = user.getQuestion();
                if (response != null) {
                    response.setChatId(chatId);
                }
                question.setChatId(chatId);
            }
            telegramUserRepository.update(user);
            try {   if(response != null) {
                    execute(response);
                    }
                    execute(question);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
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

    public void sendMessage(SendMessage message){
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
