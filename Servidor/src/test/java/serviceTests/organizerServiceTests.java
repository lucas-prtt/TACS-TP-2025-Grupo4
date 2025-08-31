package serviceTests;
import org.dominio.Enums.EventState;
import org.dominio.events.Event;
import org.dominio.events.Registration;
import org.dominio.usuarios.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.services.EventService;
import org.services.OrganizerService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class organizerServiceTests {
    private EventService eventService;
    private OrganizerService organizerService;
    private Event mockEvent;

    @BeforeEach
    void setUp() {
        eventService = Mockito.mock(EventService.class);
        organizerService = new OrganizerService(eventService);

        // Crear un evento de prueba
        mockEvent = new Event(
                "Test Event",
                "Description",
                LocalDateTime.now(),
                60,
                "Online",
                10,
                null,
                BigDecimal.TEN,
                null,
                new ArrayList<>()
        );

        // Simular participantes
        Account user1 = new Account("user1@test.com");
        Account user2 = new Account("user2@test.com");

        mockEvent.getParticipants().add(new Registration(user1));
        mockEvent.getParticipants().add(new Registration(user2));

        // Simular waitlist
        Account waitUser = new Account("wait@test.com");
        mockEvent.getWaitList().add(waitUser);
    }

    @Test
    void testGetRegistrationsFromEvent() {
        UUID eventId = mockEvent.getId();
        when(eventService.getEvent(eventId)).thenReturn(mockEvent);

        List<Account> result = organizerService.getRegistrationsFromEvent(eventId);

        assertEquals(2, result.size());
        assertEquals("user1@test.com", result.get(0).getUsername());
        assertEquals("user2@test.com", result.get(1).getUsername());
    }

    @Test
    void testGetWaitlistFromEvent() {
        UUID eventId = mockEvent.getId();
        when(eventService.getEvent(eventId)).thenReturn(mockEvent);

        Queue<Account> result = organizerService.getWaitlistFromEvent(eventId);

        assertEquals(1, result.size());
        assertEquals("wait@test.com", result.peek().getUsername());
    }

    @Test
    void testCloseRegistrations() {
        UUID eventId = mockEvent.getId();
        when(eventService.getEvent(eventId)).thenReturn(mockEvent);

        organizerService.closeRegistrations(eventId);

        assertEquals(EventState.CERRADO , mockEvent.getEventState());
    }
}
