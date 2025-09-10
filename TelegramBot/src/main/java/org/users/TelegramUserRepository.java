package org.users;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TelegramUserRepository {

    private final Map<Long, TelegramUser> users = new HashMap<>();

    public TelegramUserRepository(){
    }
    public TelegramUser addUser(Long chatId, TelegramUser user){
        users.put(chatId, user);
        return user;
    }
    public Optional<TelegramUser> getUser(Long chatid){
        if(users.containsKey(chatid))
            return Optional.of(users.get(chatid));
        else return Optional.empty();
    }


}
