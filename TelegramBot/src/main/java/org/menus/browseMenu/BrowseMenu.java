package org.menus.browseMenu;

import org.menus.MenuState;
import org.users.TelegramUser;

public class BrowseMenu extends MenuState {
    @Override
    public String respondTo(String message) {
        switch (message){
            case "/browse":
                return "NO IMPLEMENTADO"; //TODO
            case "/filterByCategory":
                return user.setMenuAndRespond(new FilterByMenu(user, "category"));
            case "/filterByTags":
                return user.setMenuAndRespond(new FilterByMenu(user, "tags"));
            case "/filterByDate":
                return user.setMenuAndRespond(new FilterByMenu(user, "maxDate"));
            case "/filterByTitle":
                return user.setMenuAndRespond(new FilterByMenu(user, "title"));
            case "/filterByTitleContains":
                return user.setMenuAndRespond(new FilterByMenu(user, "titleContains"));
            case "/filterByMinPrice":
                return user.setMenuAndRespond(new FilterByMenu(user, "minPrice"));
            case "/filterByMaxPrice":
                return user.setMenuAndRespond(new FilterByMenu(user, "maxPrice"));
            case "/showFilters":
                return "Filtros: " + String.join("\n   ",user.getFiltros());
            case "/clearFilters":
                user.clearFilters();
                return "Filtros eliminados";
            case "/lookupUUID":
                return "NO IMPLEMENTADO"; //TODO
            default:
                return "Error: Elija una opcion valida\n" + this.getQuestion();
        }
    }

    @Override
    public String getQuestion() {
        return """
                Menu de busqueda:
                /browse                  - Ver eventos con los filtros aplicados
                /filterByCategory        - Crear filtro por categorías
                /filterByTags            - Crear filtro por etiquetas
                /filterByDate            - Filtrar por fecha
                /filterByTitle           - Filtrar por título exacto
                /filterByTitleContains   - Filtrar por palabra o frase en el título
                /filterByMinPrice        - Filtrar por precio mínimo
                /filterByMaxPrice        - Filtrar por precio máximo
                /showFilters             - Ver filtros configurados
                /clearFilters            - Eliminar todos los filtros
                /lookupUUID              - Buscar evento con su id
                /start                   - Volver al menú principal
                """;
    }

    public BrowseMenu(TelegramUser user) {
        super(user);
    }
}
