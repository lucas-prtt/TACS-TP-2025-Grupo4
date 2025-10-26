package org.utils;

import org.ConfigManager;
import org.menus.MenuState;
import org.menus.userMenu.UserMenu;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public abstract class AbstractBrowseMenu<T> extends MenuState {
    protected Integer page = 0;
    protected Integer limit = ConfigManager.getInstance().getOptionalInteger("view.page.limit").orElse(5);
    protected List<T> items;


    // Obtiene los items
    protected abstract List<T> fetchItems(int page, int limit);

    // Devuelve el string corto del ítem
    protected abstract String toShortString(T item);

    // Lo que hace al elegir un item
    protected abstract void onItemSelected(T item);
    // Menu al que volver con "/back"
    protected abstract MenuState getBackMenu();

    public AbstractBrowseMenu() {
        super();
    }

    @Override
    public String respondTo(String message) {
        switch (message) {
            case "/prev":
                page = Math.max(0, page - 1);
                return null;
            case "/next":
                page++;
                return null;
            case "/back":
                user.setMenu(getBackMenu());
                return null;
            default:
                if (message.startsWith("/page")) {
                    try {
                        int numero = Integer.parseInt(message.substring(6));
                        page = Math.max(numero, 1);
                        return null;
                    } catch (Exception e) {
                        return user.getLocalizedMessage("unidentifiablePage") + "\n\n";
                    }
                }
                try {
                    int numero = Integer.parseInt(message);
                    int index = (numero - (page) * limit) - 1;
                    if (index >= 0 && index < items.size()) {
                        onItemSelected(items.get(index));
                        return user.getLocalizedMessage("itemSelected", numero);
                    }
                } catch (Exception ignored) {}

                return user.getLocalizedMessage("wrongOption")+"\n\n";
        }
    }

    @Override
    public String getQuestion() {
        AtomicInteger i = new AtomicInteger(1);
        return user.getLocalizedMessage("abstractBrowseMenuQuestion",
                String.join("\n", items.stream()
                        .map(this::toShortString)
                        .map(s -> "- " + ((page) * limit + i.getAndIncrement())  + " " + s)
                        .toList()), page+1);
    }
    @Override
    public SendMessage questionMessage() {
        List<String> optionsList = optionsList();
        return InlineMenuBuilder.localizedMenu(user, getQuestion(), List.of("/prev", "/next"), optionsList, List.of( "/back", "/start"));
    }

    public List<String> optionsList(){
        items = fetchItems(page, limit);
        List<String> optionsList = new ArrayList<>();
        for(int i = 0; i<items.size(); i++){
            optionsList.add(String.valueOf((i+1) + (page * limit)));
        }
        return optionsList;
    }
}
