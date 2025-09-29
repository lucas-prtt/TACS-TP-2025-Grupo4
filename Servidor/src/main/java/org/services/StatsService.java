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

    public long eventsAmount(){
        return  eventRepository.findAll().size();
    }
    public long registrationsAmount(){
        return  eventRepository.findAll().stream().flatMap(evento -> evento.getParticipants().stream()).toList().size();
    }

    // Cantidad de inscripciones que alguna vez estuvieron en WAITLIST
    public long waitListAmount() {
        return registrationService.findAllThatWereInWaitlist().size();
    }

    // Cantidad de inscripciones que fueron promovidas desde WAITLIST a CONFIRMED
    public long waitListPromoted() {
        return registrationService.findAllPromotedFromWaitlist().size();
    }

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