package ServiceTests;

import org.dominio.events.Event;
import org.dominio.usuarios.Account;
import org.repositories.EventRepository;
import org.repositories.AccountRepository;
import org.junit.Before;
import org.junit.Test;
import org.repositories.RegistrationRepository;
import org.services.EventService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.Assert.*;

public class EventServiceTest {
    private EventService eventService;
    private EventRepository eventRepository;
    private AccountRepository accountRepository;

  @Before
    public void setUp() {
        eventRepository = new EventRepository();
        accountRepository = new AccountRepository();
        RegistrationRepository registrationRepository = new RegistrationRepository();
        eventService = new EventService(eventRepository, accountRepository, registrationRepository);
    }

    @Test
    public void testRegisterParticipantToEvent_ConfirmedAndWaitlist() {
        // Crear evento con cupo 1
        Event event = new Event("Test Event", "desc", LocalDateTime.now().plusDays(1), 60, "CABA", 1, null, BigDecimal.ZERO, null, null);
        eventRepository.save(event);

        Account user1 = new Account();
        user1.setUsername("user1");
        accountRepository.save(user1);

        Account user2 = new Account();
        user2.setUsername("user2");
        accountRepository.save(user2);

        // Primer usuario debe quedar confirmado
        String result1 = eventService.registerParticipantToEvent(event.getId(), user1.getId());
        assertEquals("CONFIRMED", result1);
        assertEquals(1, event.getParticipants().size());
        assertEquals(0, event.getWaitList().size());
        assertEquals(1, user1.getRegistrations().size());
        assertEquals(0, user1.getWaitlists().size());

        // Segundo usuario debe quedar en waitlist
        String result2 = eventService.registerParticipantToEvent(event.getId(), user2.getId());
        assertEquals("WAITLIST", result2);
        assertEquals(1, event.getParticipants().size());
        assertEquals(1, event.getWaitList().size());
        assertEquals(0, user2.getRegistrations().size());
        assertEquals(1, user2.getWaitlists().size());
    }
}