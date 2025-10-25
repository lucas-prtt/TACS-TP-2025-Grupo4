package org.utils;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.eventServerClient.dtos.event.CategoryDTO;
import org.glassfish.jersey.internal.util.Producer;
import org.menus.MenuState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SelectCategoryMenu extends AbstractBrowseMenu<CategoryDTO>{
    private final Producer<MenuState> onBack;
    private final Consumer<CategoryDTO> onSelection;
    @Getter
    @Setter
    @NotNull
    private String startsWith = "";
    public SelectCategoryMenu(TelegramUser user, Consumer<CategoryDTO> onSelection, Producer<MenuState> onBack) {
        super(user);
        this.onSelection = onSelection;
        this.onBack = onBack;
    }
    @Override
    public String respondTo(String message){
        String fstChar = message.substring(0, 1);
        if(isLetter(fstChar)) // No empieza con / (/page, /next, etc) ni con numero (opcion elegida)
        {
            setStartsWith(message);
            return null;
        }
        if(message.equals("/eraseStartsWith")){
            setStartsWith("");
            return null;
        }
        return super.respondTo(message);
    }
    @Override
    public SendMessage questionMessage(){
        List<String> options = optionsList();
        String text = getQuestion();
        return InlineMenuBuilder.localizedMenu(user, user.getLocalizedMessage("inputGeneric", user.getLocalizedMessage("category")) + "\n" +  user.getLocalizedMessage("writeCategoryToFilter") + " \n" + text, List.of("/prev", "/next"), options, List.of("/back", "/start"), !Objects.equals(getStartsWith(), "") ?List.of("/eraseStartsWith") : List.of());
    }
    public SendMessage questionMessageWithoutBackAndStart(){
        List<String> options = optionsList();
        String text = getQuestion();
        return InlineMenuBuilder.localizedMenu(user, user.getLocalizedMessage("inputGeneric", user.getLocalizedMessage("category")) + "\n" +  user.getLocalizedMessage("writeCategoryToFilter") + " \n" + text, List.of("/prev", "/next"), options, !Objects.equals(getStartsWith(), "") ?List.of("/eraseStartsWith") : List.of());
    }



    @Override
    protected List<CategoryDTO> fetchItems(int page, int limit) {
        // TODO: Llamar API get categorias, usando page, limit y startsWith
        List<CategoryDTO> mockCategories = Stream.of(
                "Concierto", "Venta", "Curso", "Deporte", "Ceremonia",
                "Arte", "Ciencia", "Competencia", "Teatro", "Taller",
                "Exposición", "Seminario", "Conferencia", "Festival", "Viaje",
                "Campamento", "Deporte Extremo", "Música", "Danza", "Literatura",
                "Fotografía", "Cine", "Cultura", "Educación", "Medicina",
                "Tecnología", "Gastronomía", "Networking", "Voluntariado", "Negocios",
                "Religión", "Yoga", "Meditación", "Idiomas", "Programación",
                "Historia", "Filosofía", "Astronomía", "Robótica", "Ecología",
                "Moda", "Belleza", "Fitness", "Marketing", "Psicología",
                "Arquitectura", "Diseño", "Videojuegos", "Viajes de Aventura", "Emprendimiento"
        ).map(CategoryDTO::new).toList();
        mockCategories = mockCategories.stream().filter(c -> c.getTitle().toLowerCase().startsWith(startsWith.toLowerCase())).toList();
        int fromIndex = (page) * limit;
        int toIndex = Math.min(fromIndex + limit, mockCategories.size());
        if (fromIndex >= mockCategories.size()) {
            return List.of();
        }
        return mockCategories.subList(fromIndex, toIndex);
    }

    @Override
    protected String toShortString(CategoryDTO item) {
        return item.toShortString();
    }

    @Override
    protected void onItemSelected(CategoryDTO item) {
        onSelection.accept(item);
    }


    @Override
    protected MenuState getBackMenu() {
        return onBack.call();
    }
    private boolean isLetter(String character) {
        return character != null
                && character.length() == 1
                && LETTERS.contains(character.toLowerCase());
    }
    private static final Set<String> LETTERS = "abcdefghijklmnñopqrstuvwxyz<"
            .chars()
            .mapToObj(c -> String.valueOf((char)c))
            .collect(Collectors.toUnmodifiableSet());
}
