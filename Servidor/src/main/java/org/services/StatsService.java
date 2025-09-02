package org.services;

import org.DTOs.StatsDTO;
import org.model.enums.RegistrationState;
import org.repositories.EventRepository;
import org.repositories.RegistrationRepository;
import org.springframework.stereotype.Service;

@Service
public class StatsService {


    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;

    public StatsService(EventRepository eventRepository, RegistrationRepository registrationRepository) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
    }

    public long eventsAmount(){
        return  eventRepository.getAll().size();
    }
    public long registrationsAmount(){
        return  eventRepository.getAll().stream().flatMap(evento -> evento.getParticipants().stream()).toList().size();
    }

    // Cantidad de inscripciones que alguna vez estuvieron en WAITLIST
    public long waitListAmount() {
        return registrationRepository.findAllThatWereInWaitlist().size();
    }

    // Cantidad de inscripciones que fueron promovidas desde WAITLIST a CONFIRMED
    public long waitListPromoted() {
        return registrationRepository.findAllPromotedFromWaitlist().size();
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