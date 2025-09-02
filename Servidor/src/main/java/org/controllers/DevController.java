package org.controllers;

import org.dominio.events.Event;
import org.dominio.events.Registration;
import org.dominio.usuarios.Account;
import org.repositories.EventRepository;
import org.services.EventService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/dev")
public class DevController {

    private final EventRepository eventRepository;

    public DevController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @PostMapping("/load-sample")
    public String loadSampleData() {

        Account organizer = new Account();

        // Crear un evento con los campos obligatorios
        Event event = new Event(
                "Tech Meetup",
                "Evento de prueba para Postman",
                LocalDateTime.now().plusDays(7), // empieza en 7 días
                120,                             // duración en minutos
                "Auditorio Central",
                2,                               // máximo participantes
                null,                            // mínimo opcional
                new BigDecimal("100.00"),        // precio
                null,                            // categoría opcional
                null,                             // tags opcionales
                organizer
        );

        // Crear algunos usuarios de prueba
        Account acc1 = new Account( "alice@example.com");
        Account acc2 = new Account( "bob@example.com");
        Account acc3 = new Account( "charlie@example.com");

        // Crear inscripciones y registrarlas en el evento
        Registration reg1 = new Registration( acc1);
        reg1.setEvent(event);
        Registration reg2 = new Registration( acc2);
        reg2.setEvent(event);
        Registration reg3 = new Registration(acc3);
        reg3.setEvent(event);

        event.registerParticipant(reg1);
        event.registerParticipant(reg2);
        event.registerParticipant(reg3); // este debería ir a la waitlist

        // Guardar el evento con el EventService
        eventRepository.save(event);

        return "Loaded test event with ID: " + event.getId();
    }
}
