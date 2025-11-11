package org.repositories;

import java.util.*;
import java.util.List;

import org.model.events.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends MongoRepository<Event, UUID> {
    // Recibe el titulo del evento a buscar como String y devuelve una lista inmutable de Eventos del mismo titulo
    public List<Event> findByTitle(String title);

    // Recibe un String y busca todos los eventos que tengan en su título ese string, devolviéndolos como lista inmutable de Event
    public List<Event> findByTitleContains(String title);

    Page<Event> findByOrganizerId(UUID organizerId, Pageable pageable);
}