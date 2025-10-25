package org.utils;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.eventServerClient.dtos.event.CategoryDTO;
import org.glassfish.jersey.internal.util.Producer;
import org.menus.MenuState;
import org.users.TelegramUser;

import java.util.List;
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
}
