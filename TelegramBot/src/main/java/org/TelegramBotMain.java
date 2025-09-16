package org;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook;
import org.telegram.telegrambots.meta.api.methods.updates.GetWebhookInfo;
import org.telegram.telegrambots.meta.api.objects.WebhookInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TelegramBotMain {
    public static void main(String[] args) {
        System.out.println("Conexion al servidor de eventos:");
        System.out.println("IP: " + ConfigManager.getInstance().get("server.ip"));
        System.out.println("Port: " + ConfigManager.getInstance().get("server.port"));

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
            System.out.println("\nCargando bot...");
            BotEventos bot = new BotEventos(TOKEN, NAME);

            WebhookInfo webhookInfo = bot.execute(new GetWebhookInfo());
            if (webhookInfo.getUrl() == null || webhookInfo.getUrl().isEmpty()) {
                System.out.println("No hay webhook configurado.");
            } else {
                System.out.println("Webhook activo en: " + webhookInfo.getUrl());
                System.out.println("Eliminando webhook...");
                boolean deleted = bot.execute(new DeleteWebhook());
                System.out.println("Webhook eliminado: " + deleted);
            }

            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);

            System.out.println("Bot iniciado correctamente.");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}