package RepositoryTests;
import org.model.events.Event;
import org.model.accounts.Account;
import org.junit.Test;
import org.repositories.EventRepository;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventRepositoryTest {
    EventRepository eventRepository = new EventRepository();

    public List<Event> createEvents(){
        Account organizador = new Account();
        organizador.setUsername("organizador");
        Event evento1 = Event.Builder().setTitle("Fiesta de cumpleaños").setDescription("Juan perez cumple años").setLocation("CABA").setPrice(new BigDecimal(0)).setMaxParticipants(40).setStartDateTime(LocalDateTime.now().plusDays(1)).setDurationMinutes(180).setOrganizer(organizador).build();
        Event evento2 = Event.Builder().setTitle("Concierto de Jazz").setDescription("Noche de jazz en vivo").setLocation("Teatro Colón").setPrice(new BigDecimal("1500")).setMaxParticipants(200).setStartDateTime(LocalDateTime.now().plusDays(5)).setDurationMinutes(120).setOrganizer(organizador).build();
        Event evento3 = Event.Builder().setTitle("Taller de cerámica").setDescription("Clase práctica para principiantes").setLocation("Villa Crespo").setPrice(new BigDecimal("800")).setMaxParticipants(15).setStartDateTime(LocalDateTime.now().plusWeeks(1)).setDurationMinutes(90).setOrganizer(organizador).build();
        Event evento4 = Event.Builder().setTitle("Cine al aire libre").setDescription("Proyección de clásicos en blanco y negro").setLocation("Parque Centenario").setPrice(new BigDecimal("0")).setMaxParticipants(300).setStartDateTime(LocalDateTime.now().plusDays(2)).setDurationMinutes(150).setOrganizer(organizador).build();
        Event evento5 = Event.Builder().setTitle("Charla de IA").setDescription("Aplicaciones de inteligencia artificial en entornos empresariales").setLocation("UTN - FRBA - Sede Medrano").setPrice(new BigDecimal("500")).setMaxParticipants(100).setStartDateTime(LocalDateTime.now().plusDays(3)).setDurationMinutes(60).setOrganizer(organizador).build();
        Event evento6 = Event.Builder().setTitle("Festival gastronómico").setDescription("Comidas del mundo en un solo lugar").setLocation("Costanera Sur").setPrice(new BigDecimal("200")).setMaxParticipants(1000).setStartDateTime(LocalDateTime.now().plusDays(10)).setDurationMinutes(360).setOrganizer(organizador).build();
        Event evento7 = Event.Builder().setTitle("Taller de reparacion de guitarras").setDescription("Aprendé con músicos profesionales").setLocation("Palermo").setPrice(new BigDecimal("1000")).setMaxParticipants(25).setStartDateTime(LocalDateTime.now().plusDays(4)).setDurationMinutes(180).setOrganizer(organizador).build();
        Event evento8 = Event.Builder().setTitle("Maratón solidario").setDescription("Corré por una buena causa").setLocation("Puerto Madero").setPrice(new BigDecimal("0")).setMaxParticipants(500).setStartDateTime(LocalDateTime.now().plusWeeks(2)).setDurationMinutes(240).setOrganizer(organizador).build();
        Event evento9 = Event.Builder().setTitle("Curso de fotografía").setDescription("Técnicas básicas y uso de cámara").setLocation("Recoleta").setPrice(new BigDecimal("1200")).setMaxParticipants(20).setStartDateTime(LocalDateTime.now().plusDays(7)).setDurationMinutes(180).setOrganizer(organizador).build();
        Event evento10 = Event.Builder().setTitle("Torneo de ajedrez").setDescription("Competencia abierta con premios").setLocation("Club Argentino de Ajedrez").setPrice(new BigDecimal("300")).setMaxParticipants(50).setStartDateTime(LocalDateTime.now().plusDays(6)).setDurationMinutes(240).setOrganizer(organizador).build();
        Event evento11 = Event.Builder().setTitle("Exposición de arte").setDescription("Galería de artistas locales").setLocation("San Telmo").setPrice(new BigDecimal("100")).setMaxParticipants(150).setStartDateTime(LocalDateTime.now().plusDays(8)).setDurationMinutes(180).setOrganizer(organizador).build();
        return List.of(evento1, evento2, evento3, evento4, evento5, evento6, evento7, evento8, evento9, evento10, evento11);

    }

    @Test
    public void createEventsWithBuilder(){
        createEvents();
    }
    @Test
    public void saveEvents(){
        List <Event> events = new ArrayList<>(createEvents());
        events.forEach(ev -> eventRepository.save(ev));
    }
    @Test
    public void loadEvents(){
        List <Event> events = new ArrayList<>(createEvents());
        events.forEach(ev -> eventRepository.save(ev));
        assert eventRepository.getAll().size() == 11;
        assert eventRepository.getAll().stream().findFirst().get().getTitle() == "Fiesta de cumpleaños";
    }
    @Test
    public void findByTitle(){
        List <Event> events = new ArrayList<>(createEvents());
        events.forEach(ev -> eventRepository.save(ev));
        assert eventRepository.findByTitle("Taller de cerámica").getFirst().getDescription() == "Clase práctica para principiantes";
    }
    @Test
    public void findByTitleContains(){
        List <Event> events = new ArrayList<>(createEvents());
        events.forEach(ev -> eventRepository.save(ev));
        assert eventRepository.findByTitleContains("IA").getFirst().getLocation() == "UTN - FRBA - Sede Medrano";
    }
    @Test
    public void findByTitleContainsMultiple(){
        List <Event> events = new ArrayList<>(createEvents());
        events.forEach(ev -> eventRepository.save(ev));
        assert eventRepository.findByTitleContains("de").size() == 8;
    }
    @Test
    public void findByCustom(){
        List <Event> events = new ArrayList<>(createEvents());
        events.forEach(ev -> eventRepository.save(ev));
        assert eventRepository.findBy(event -> event.getStartDateTime().isBefore(LocalDateTime.now().plusWeeks(1).plusHours(1))).size() == 8;
    }
}
