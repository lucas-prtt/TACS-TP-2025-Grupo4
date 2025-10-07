package org.services;

import org.DTOs.StatsDTO;
import org.repositories.EventRepository;
import org.springframework.stereotype.Service;

@Service
public class StatsService {
    private final RegistrationService registrationService;
    private final EventRepository eventRepository;

    public StatsService(EventRepository eventRepository, RegistrationService registrationService) {
        this.eventRepository = eventRepository;
        this.registrationService = registrationService;
    }

    /**
     * Devuelve la cantidad total de eventos.
     * @return cantidad de eventos
     */
    public long eventsAmount(){
        return  eventRepository.findAll().size();
    }
    /**
     * Devuelve la cantidad total de inscripciones.
     * @return cantidad de inscripciones
     */
    public long registrationsAmount(){
        return  eventRepository.findAll().stream().flatMap(evento -> evento.getParticipants().stream()).toList().size();
    }

    /**
     * Devuelve la cantidad de inscripciones que alguna vez estuvieron en WAITLIST.
     * @return cantidad de inscripciones en WAITLIST
     */
    public long waitListAmount() {
        return registrationService.findAllThatWereInWaitlist().size();
    }

    /**
     * Devuelve la cantidad de inscripciones promovidas de WAITLIST a CONFIRMED.
     * @return cantidad de inscripciones promovidas
     */
    public long waitListPromoted() {
        return registrationService.findAllPromotedFromWaitlist().size();
    }

    /**
     * Devuelve un objeto StatsDTO con estadísticas generales del sistema.
     * @return objeto StatsDTO con métricas de eventos e inscripciones
     */
    public StatsDTO getStats() {
    long eventsCount = eventsAmount();
    long registrationsCount = registrationsAmount();
    long waitlistPromotions = waitListPromoted();
    long waitlistTotalCount = waitListAmount();

    double conversionRate = waitlistTotalCount == 0 ? 0.0 :
        (double) waitlistPromotions / waitlistTotalCount;

    return new StatsDTO(
        eventsCount,
        registrationsCount,
        waitlistPromotions,
        conversionRate
    );
    }
}