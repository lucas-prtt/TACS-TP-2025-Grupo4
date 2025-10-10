package org.menus;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.utils.I18nManager;
import org.utils.InlineMenuBuilder;

import java.util.*;

public class LanguageMenu extends MenuState {
    @Override
    public String respondTo(String message) {
        if(message.equals("/back")){
            user.setMenu(new MainMenu(user));
            return null;
        }
        if(message.charAt(0) != '/'){
            return user.getLocalizedMessage("wrongOption");
        }
        String languageCode = message.substring(1);
        if(I18nManager.getLanguageKeys().contains(languageCode)){
            user.setLang(languageCode);
            user.setMenu(new MainMenu(user));
            return user.getLocalizedMessage("languageSelected", user.getLocalizedMessage("language"));
        }else{
            return user.getLocalizedMessage("wrongOption");
        }
    }

    @Override
    public String getQuestion() {
        return user.getLocalizedMessage("languageMenuQuestion");
    }

    @Override
    @SuppressWarnings("unchecked")
    public SendMessage questionMessage() {
        Set<String> keys = I18nManager.getLanguageKeys();
        List<Map<String, String>> botones = new ArrayList<>();
        for(String key : keys){
            botones.add(Map.of(I18nManager.get("languageEmoji", key) + " - "+ I18nManager.get("language", key), "/"+key));
        }
        botones.add(Map.of(user.getLocalizedMessage("/back"), "/back"));
        return InlineMenuBuilder.menu(getQuestion(), botones.toArray(new Map[0]));
    }

    public LanguageMenu(TelegramUser user) {
        super(user);
    }
}
