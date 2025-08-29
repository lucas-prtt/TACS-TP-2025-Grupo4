package org.repositories;

import org.dominio.events.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class EventRepository {
    List<Event> events = new ArrayList<Event>();

    // Recibe una uuid del evento a buscar
    // Devuelve el evento guardado
    // Si no hay un elemento guardado, lanza NoSuchElementException
    public Event findById(UUID uuid) throws NoSuchElementException {
        return events.stream().filter(event -> event.getId() == uuid).findFirst().get();
    }

    // Recibe el evento a añadir
    // Almacena el evento en la base de datos
    // Si esta presente uno con la misma id, lo sobreescribe
    // Si el evento recibido es null, lanza NullPointerException
    public void save(Event newEvent) throws NullPointerException{
        if (newEvent == null) throw new NullPointerException("newEvent value is null");
        Event existingEvent = findById(newEvent.getId());
        if(existingEvent == null){
            events.add(newEvent);
        }
        else {
            events.remove(existingEvent);
            events.add(newEvent);
        }
    }
}
