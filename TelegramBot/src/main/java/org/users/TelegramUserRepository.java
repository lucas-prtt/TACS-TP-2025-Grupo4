package org.users;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class TelegramUserRepository {

    private final Cache<Long, TelegramUser> users;

    public TelegramUserRepository() {
        users = Caffeine.newBuilder()
                .expireAfterAccess(3, TimeUnit.DAYS)
                .expireAfterWrite(6, TimeUnit.DAYS)
                .maximumSize(500_000)
                .removalListener((Long key, TelegramUser user, RemovalCause cause) -> {
                    System.out.println("Usuario " + key + " eliminado: " + cause);
                })
                .build();
    }



    public TelegramUser addUser(Long chatId, TelegramUser user) {
        users.put(chatId, user);
        return user;
    }

    public Optional<TelegramUser> getUser(Long chatId) {
        return Optional.ofNullable(users.getIfPresent(chatId));
    }

    public void removeUser(Long chatId) {
        users.invalidate(chatId);
    }

    public long getActiveUserCount() {
        return users.estimatedSize();
    }

}
