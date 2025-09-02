package org.services;

import org.DTOs.StatsDTO;
import org.dominio.events.Event;
import org.repositories.EventRepository;
import org.springframework.stereotype.Service;

@Service
public class StatsService {


    private final EventRepository eventRepository;
    public StatsService(EventRepository eventRepository){
        this.eventRepository = eventRepository;
    }

    public long eventsAmount(){
        return  eventRepository.getAll().size();
    }
    public long registrationsAmount(){
        return  eventRepository.getAll().stream().flatMap(evento -> evento.getParticipants().stream()).toList().size();
    }

    public long waitListAmount(){
        return  eventRepository.getAll().stream().flatMap(evento -> evento.getWaitList().stream()).toList().size();
    }

    public StatsDTO getStats() {
        long eventsCount = eventsAmount();
        long registrationsCount = registrationsAmount();
        long waitlistPromotions = waitListAmount();

        double conversionRate = registrationsCount == 0 ? 0.0 :
                (double) waitlistPromotions / registrationsCount;

        return new StatsDTO(
                eventsCount,
                registrationsCount,
                waitlistPromotions,
                conversionRate
        );
    }
}