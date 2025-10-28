package org.users;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import org.BotEventos;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.utils.InlineMenuBuilder;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CacheTelegramUserRepository implements TelegramUserRepository{

    private final Cache<Long, TelegramUser> users;

    public CacheTelegramUserRepository(BotEventos botEventos) {
        users = Caffeine.newBuilder()
                .expireAfterAccess(3, TimeUnit.DAYS)
                .expireAfterWrite(6, TimeUnit.DAYS)
                .maximumSize(250_000) // Max 250.000 sesiones simultaneas
                .removalListener((Long key, TelegramUser user, RemovalCause cause) -> {
                    SendMessage message = InlineMenuBuilder.localizedMenu(user, user.getLocalizedMessage("sessionExpired"), List.of("/start"));
                    message.setChatId(key);
                    botEventos.sendMessage(message);
                })
                .build();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(users::cleanUp, 0, 12, TimeUnit.HOURS);
    }



    public TelegramUser addUser(Long chatId, TelegramUser user) {
        users.put(chatId, user);
        return user;
    }

    public Optional<TelegramUser> getUser(Long chatId) {
        return Optional.ofNullable(users.getIfPresent(chatId));
    }

    @Override
    public void update(TelegramUser user) {
    }

}
