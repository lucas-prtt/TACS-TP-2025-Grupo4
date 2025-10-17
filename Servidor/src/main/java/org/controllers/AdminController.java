package org.controllers;


import org.DTOs.StatsDTO;
import org.services.EventService;
import org.services.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
public class AdminController {

    private final StatsService statsService;

    public AdminController(EventService eventService, StatsService statsService) {
        this.statsService = statsService;
    }

    /**
     * Obtiene las estadísticas generales del sistema para el panel de administración.
     * @return ResponseEntity con el objeto StatsDTO que contiene las estadísticas
     */
    @GetMapping("/stats")
    public ResponseEntity<StatsDTO> getStats() {
        return ResponseEntity.ok(statsService.getStats());
    }
}