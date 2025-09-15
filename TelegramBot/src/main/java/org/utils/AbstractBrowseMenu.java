package org.utils;

import org.ConfigManager;
import org.menus.MenuState;
import org.menus.userMenu.UserMenu;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public abstract class AbstractBrowseMenu<T> extends MenuState {
    protected Integer page = 1;
    protected Integer limit = ConfigManager.getInstance().getOptionalInteger("view.page.limit").orElse(5);
    protected List<T> items;


    // Obtiene los items
    protected abstract List<T> fetchItems(int page, int limit);

    // Devuelve el string corto del ítem
    protected abstract String toShortString(T item);

    // Genera el nuevo menú al seleccionar un ítem
    protected abstract MenuState itemSelectedMenu(T item);

    // Menu al que volver con "/back"
    protected abstract MenuState getBackMenu();

    public AbstractBrowseMenu(TelegramUser user) {
        super(user);
    }

    @Override
    public String respondTo(String message) {
        switch (message) {
            case "/prev":
                page = Math.max(1, page - 1);
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
                        return "No se pudo identificar la página\n\n";
                    }
                }
                try {
                    int numero = Integer.parseInt(message.substring(1));
                    int index = (numero - (page - 1) * limit) - 1;
                    if (index >= 0 && index < items.size()) {
                        user.setMenu(itemSelectedMenu(items.get(index)));
                        return "Item " + numero + " elegido!";
                    }
                } catch (Exception ignored) {}

                return "No se eligió una opción válida\n\n";
        }
    }

    @Override
    public String getQuestion() {
        AtomicInteger i = new AtomicInteger(1);
        items = fetchItems(page, limit);
        return "Navegando resultados:\n\n" +
                String.join("\n", items.stream()
                        .map(this::toShortString)
                        .map(s -> "/" + ((page - 1) * limit + i.getAndIncrement())  + " " + s)
                        .toList()) +
                "\n\nPágina " + page + "\n\n" +
                "/prev    -   /next\n" +
                "/page <n>     --> Ir a página n\n" +
                "/back         --> Volver\n" +
                "/start        --> Menu Principal";
    }
    @Override
    public SendMessage questionMessage() {
        SendMessage message = InlineMenuBuilder.menu(getQuestion(), List.of("/prev", "/next"), List.of("/back", "/start"));
        return message;
    }
}
