package org.users;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import org.BotEventos;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.utils.InlineMenuBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public interface TelegramUserRepository {

    public TelegramUser addUser(Long chatId, TelegramUser user);

    public Optional<TelegramUser> getUser(Long chatId);

    public void update(TelegramUser user);
}
