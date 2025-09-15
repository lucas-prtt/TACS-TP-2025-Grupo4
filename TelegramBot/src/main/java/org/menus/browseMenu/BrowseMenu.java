package org.menus.browseMenu;

import org.menus.MenuState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.utils.InlineMenuBuilder;

import java.util.List;

public class BrowseMenu extends MenuState {
    @Override
    public String respondTo(String message) {
        switch (message){
            case "/browse":
                user.setMenu(new BrowseEventsMenu(user));
                return null;
            case "/filterByCategory":
                user.setMenu(new FilterByMenu(user, "category"));
                return null;
            case "/filterByTags":
                user.setMenu(new FilterByMenu(user, "tags"));
                return null;
            case "/filterByDate":
                user.setMenu(new FilterByMenu(user, "maxDate"));
                return null;
            case "/filterByTitle":
                user.setMenu(new FilterByMenu(user, "title"));
                return null;
            case "/filterByTitleContains":
                user.setMenu(new FilterByMenu(user, "titleContains"));
                return null;
            case "/filterByMinPrice":
                user.setMenu(new FilterByMenu(user, "minPrice"));
                return null;
            case "/filterByMaxPrice":
                user.setMenu(new FilterByMenu(user, "maxPrice"));
                return null;
            case "/showFilters":
                return "Filtros: \n" + String.join("\n   ",user.getFiltros());
            case "/clearFilters":
                user.clearFilters();
                return "Filtros eliminados\n\n";
            case "/lookupUUID":
                user.setMenu(new LookUpEventByUUIDMenu(user));
                return null;
            default:
                return "Error: Elija una opcion valida\n";
        }
    }

    @Override
    public String getQuestion() {
        return
                user.getFiltros().size() + " filtro" + (user.getFiltros().size() == 1 ? "" : "s") + " aplicados\n\n" +
                """
                Menu de busqueda:
                
                /filterByCategory
                    - Crear filtro por categorías
                /filterByTags
                    - Crear filtro por etiquetas
                /filterByDate
                    - Crear filtro por fecha
                /filterByTitle
                    - Crear filtro por título exacto
                /filterByTitleContains
                    - Crear filtro por palabra o frase en el título
                /filterByMinPrice
                    - Crear filtro por precio mínimo
                /filterByMaxPrice
                    - Crear filtro por precio máximo
                
                /showFilters
                    - Ver filtros configurados
                /clearFilters
                    - Eliminar todos los filtros
                
                /lookupUUID
                    - Buscar evento con su id
                /browse
                    - Ver eventos con los filtros aplicados
                
                /start
                    - Volver al menú principal
                """;
    }

    @Override
    public SendMessage questionMessage() {
        SendMessage message = InlineMenuBuilder.menu(getQuestion(), List.of("/filterByCategory", "/filterByTags"), List.of( "/filterByDate",
                "/filterByTitle"), List.of("/filterByTitleContains", "/filterByMinPrice") , List.of( "/filterByMaxPrice", "/lookupUUID"), List.of("/showFilters", "/clearFilters") , List.of( "/browse"), List.of("/start"));
        return message;
    }

    public BrowseMenu(TelegramUser user) {
        super(user);
    }
}
