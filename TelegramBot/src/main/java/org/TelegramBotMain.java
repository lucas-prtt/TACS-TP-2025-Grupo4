package org;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TelegramBotMain {
    public static void main(String[] args) {
        System.out.println("Conexion al servidor de eventos:");
        System.out.println("IP: " + ConfigManager.getInstance().get("server.ip"));
        System.out.println("Port: " + ConfigManager.getInstance().get("server.port"));
        System.out.println("\nCargando bot...");

        String NAME = System.getenv("EVENTOS_TELEGRAM_BOT_USERNAME");
        if (NAME == null) {
            System.err.println("La variable de entorno EVENTOS_TELEGRAM_BOT_NOMBRE no está definida");
            return;
        }
        String TOKEN = System.getenv("EVENTOS_TELEGRAM_BOT_TOKEN");
        if (TOKEN == null) {
            System.err.println("La variable de entorno EVENTOS_TELEGRAM_BOT_TOKEN no está definida");
            return;
        }



        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new BotEventos(TOKEN, NAME));
            System.out.println("Bot iniciado correctamente.");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}