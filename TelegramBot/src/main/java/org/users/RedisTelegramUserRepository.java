package org.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ConfigManager;
import redis.clients.jedis.Jedis;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Optional;

public class RedisTelegramUserRepository implements TelegramUserRepository {
    private Jedis jedis = new Jedis(ConfigManager.getInstance().getOptional("redis.ip").orElse("localhost"), ConfigManager.getInstance().getOptionalInteger("redis.port").orElse(8001));
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }
    public TelegramUser addUser(Long chatId, TelegramUser user) {
        try {
            String json = objectMapper.writeValueAsString(user);
            jedis.set(chatId.toString(), json);
            return user;
        } catch (Exception e) {
            throw new RuntimeException("Error al serializar el usuario", e);
        }
    }

    public Optional<TelegramUser> getUser(Long chatId) {
        try {
            String json = jedis.get(chatId.toString());
            if (json == null) return Optional.empty();
            TelegramUser user = objectMapper.readValue(json, TelegramUser.class);
            return Optional.of(user);
        } catch (Exception e) {
            throw new RuntimeException("Error al deserializar el usuario", e);
        }
    }

    public void update(TelegramUser user) {
        if (user.getChatId() == null) {
            throw new IllegalArgumentException("El usuario debe tener chatId para actualizar");
        }
        addUser(user.getChatId(), user);
    }
}
