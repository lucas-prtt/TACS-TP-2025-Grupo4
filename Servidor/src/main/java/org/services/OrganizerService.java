package org.services;

import org.model.enums.RegistrationState;
import org.model.events.Event;
import org.model.events.Registration;
import org.springframework.stereotype.Service;
import org.utils.PageSplitter;
import java.util.List;
import java.util.UUID;

@Service
public class OrganizerService {
    private final EventService eventService;

    public OrganizerService(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Valida que el usuario sea el organizador del evento.
     * @param accountId ID del usuario
     * @param event Evento a validar
     * @throws SecurityException si el usuario no es el organizador
     */
    private void validateOrganizer(UUID accountId, Event event) {
        if (!event.getOrganizer().getId().equals(accountId)) {
            throw new SecurityException("No tienes permisos para gestionar este evento.");
        }
    }

    /**
     * Devuelve la lista de inscripciones de un evento, filtrando por estado y paginando.
     * @param eventId ID del evento
     * @param state Estado de la inscripción (opcional)
     * @param page Número de página (opcional)
     * @param limit Cantidad de elementos por página (opcional)
     * @return Lista paginada de inscripciones
     */
    public List<Registration> getRegistrationsFromEvent(UUID eventId, RegistrationState state, Integer page, Integer limit) {
        Event event = eventService.getEvent(eventId);
        return PageSplitter.getPageList(event.getParticipants().stream().filter(registration -> state == null || registration.getCurrentState() == state).toList(), page, limit);
    }

    /**
     * Devuelve la lista de espera de un evento, validando que el usuario sea el organizador.
     * @param accountId ID del usuario
     * @param eventId ID del evento
     * @return Lista de inscripciones en espera
     * @throws SecurityException si el usuario no es el organizador
     */
    public List<Registration> getWaitlistFromEvent(UUID accountId, UUID eventId) {
        Event event = eventService.getEvent(eventId);
        validateOrganizer(accountId, event);
        return event.getWaitList();
    }

    /**
     * Cierra las inscripciones de un evento, validando que el usuario sea el organizador.
     * @param accountId ID del usuario
     * @param eventId ID del evento
     * @throws SecurityException si el usuario no es el organizador
     */
    public void closeRegistrations(UUID accountId, UUID eventId) {
        Event event = eventService.getEvent(eventId);
        validateOrganizer(accountId, event);
        event.closeRegistrations();
    }
}
