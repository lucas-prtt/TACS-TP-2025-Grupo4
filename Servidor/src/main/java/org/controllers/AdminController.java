package org.controllers;


import jakarta.annotation.PostConstruct;
import org.DTOs.StatsDTO;
import org.model.events.Category;
import org.services.CategoryService;
import org.services.EventService;
import org.services.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Stream;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
public class AdminController {

    private final StatsService statsService;
    private final CategoryService categoryService;

    public AdminController(EventService eventService, StatsService statsService, CategoryService categoryService, CategoryService categoryService1) {
        this.statsService = statsService;
        this.categoryService = categoryService1;
    }
    @PostConstruct
    private void inicializarCategorias(){
        Stream.of(
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
        ).forEach(cat -> {
            try {
                categoryService.createCategory(new Category(cat));
            }catch (Exception ignored){}
        });

    }

    /**
     * Obtiene las estadísticas generales del sistema para el panel de administración.
     * @return ResponseEntity con el objeto StatsDTO que contiene las estadísticas
     */
    @GetMapping("/stats")
    public ResponseEntity<StatsDTO> getStats() {
        return ResponseEntity.ok(statsService.getStats());
    }

    @PostMapping("/categories")
    public ResponseEntity<Category> addCategory(@RequestBody Category category){
        return ResponseEntity.ok(categoryService.createCategory(new Category(category.getTitle())));
    }
    @DeleteMapping("/categories/{titulo}")
    public ResponseEntity<Void> addCategory(@PathVariable(name = "titulo") String category){
        categoryService.deleteCategoryByTitle(category);
        return ResponseEntity.noContent().build();
    }
}