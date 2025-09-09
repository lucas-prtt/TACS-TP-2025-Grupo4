package org;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TelegramBotMain {
    public static void main(String[] args) {

        System.out.println("Cargando bot...");

        String TOKEN = System.getenv("EVENTOS_TELEGRAM_BOT_TOKEN");
        if (TOKEN == null) {
            System.err.println("La variable de entorno EVENTOS_TELEGRAM_BOT_TOKEN no está definida");
            return;
        }

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new BotEventos(TOKEN));
            System.out.println("Bot iniciado correctamente.");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}