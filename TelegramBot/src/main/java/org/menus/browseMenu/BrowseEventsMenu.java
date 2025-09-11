package org.menus.browseMenu;

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
        switch (message){
            case "/prev":
                page--;
                return getQuestion();
            case "/next":
                page++;
                return getQuestion();
            case "/back":
                return user.setMenuAndRespond(new BrowseMenu(user));
            default:
                if(message.startsWith("/page")){
                    try {
                        int numero = Integer.parseInt(message.substring(6));
                        page = Math.max(numero, 1);
                        return getQuestion();
                    }
                    catch (Exception e){
                        return "No se pudo identificar la pagina\n\n" + getQuestion();
                    }
                }
                try {
                    int numero = Integer.parseInt(message.substring(1));
                    return user.setMenuAndRespond(new CheckEventMenu(user, events.get((numero-(page-1)*limit)-1)));
                }catch (Exception ignored){}

                return "No se eligio una opcion valida\n\n" + getQuestion();
        }

    }

    @Override
    public String getQuestion() {
        AtomicInteger i = new AtomicInteger(1);
        events = ApiClient.getEventsByFilters(user.getAllFiltersAsQueryParams(), page, limit);
        return "Navegando eventos " + (user.getFiltros().isEmpty() ? "sin filtros: \n\n" : "filtrados: \n\n") +
        String.join("\n", events.stream().map(EventDTO::asShortString).map(s -> {return "/" + ((page-1) * limit + i.getAndIncrement()) + s;}).toList())+
                "\n\nPagina " + page+"\n\n" +
                "/prev    -   /next\n" +
                "/page <n>     --> Ir a pagina n\n" +
                "/back         --> volver";
    }

    public BrowseEventsMenu(TelegramUser user) {
        super(user);
    }
}
