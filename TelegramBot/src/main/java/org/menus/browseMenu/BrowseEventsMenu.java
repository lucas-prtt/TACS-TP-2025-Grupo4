package org.menus.browseMenu;

import jdk.management.jfr.EventTypeInfo;
import org.ConfigManager;
import org.eventServerClient.ApiClient;
import org.eventServerClient.dtos.event.EventDTO;
import org.menus.MenuState;
import org.users.TelegramUser;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BrowseEventsMenu extends MenuState {
    Integer page = 1;
    Integer limit = ConfigManager.getInstance().getOptionalInteger("view.page.limit").orElse(5);
    List<EventDTO> events;
    @Override
    public String respondTo(String message) {

        

        return user.getMenu().getQuestion();
    }

    @Override
    public String getQuestion() {
        AtomicInteger i = new AtomicInteger(1);
        events = ApiClient.getEventsByFilters(user.getAllFiltersAsQueryParams(), page, limit);
        return "Navegando eventos" + (user.getFiltros().isEmpty() ? "sin filtros: \n" : " filtrados: \n") +
        String.join("\n", events.stream().map(EventDTO::asShortString).map(s -> {return i.getAndIncrement() + s;}).toList())+
                "\n\nPagina " + page+"\n\n" +
                "/prev    -   /next\n" +
                "/check {index}    --> Examinar evento\n" +
                "/back             --> volver";
    }

    public BrowseEventsMenu(TelegramUser user) {
        super(user);
    }
}
