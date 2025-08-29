package org.repositories;

import org.dominio.events.Event;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

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

    // Recibe el titulo del evento a buscar como String y devuelve una lista inmutable de Eventos del mismo titulo
    public List<Event> findByTitle(String goalTitle) throws NoSuchElementException{
        return findBy(event -> Objects.equals(event.getTitle(), goalTitle));
    }
    // Recibe un String y busca todos los eventos que tengan en su título ese string, devolviéndolos como lista inmutable de Event
    public List<Event> findByTitleContains(String subString){
        return findBy(event -> event.getTitle().contains(subString));
    }

    // Recibe un String y busca todos los eventos que tengan en su título ese string, devolviéndolos como lista inmutable de Event
    public List<Event> findBy(Predicate<Event> condition){
        return events.stream().filter(condition).toList();
    }
}
