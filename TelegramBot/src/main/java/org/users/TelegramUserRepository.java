package org.users;

import java.util.Optional;

public interface TelegramUserRepository {

    public TelegramUser addUser(Long chatId, TelegramUser user);

    public Optional<TelegramUser> getUser(Long chatId);

    public void update(TelegramUser user);
}
