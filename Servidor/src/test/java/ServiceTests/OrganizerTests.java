package ServiceTests;
import org.model.enums.EventState;
import org.model.events.Event;
import org.model.events.Registration;
import org.model.accounts.Account;
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
public class OrganizerTests {
    private EventService eventService;
    private OrganizerService organizerService;
    private Event mockEvent;

    @BeforeEach
    void setUp() {
        eventService = Mockito.mock(EventService.class);
        organizerService = new OrganizerService(eventService);

        Account organizer = new Account();

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
                new ArrayList<>(),
                organizer
        );

        // Simular participantes
        Account user1 = new Account("user1@test.com");
        Account user2 = new Account("user2@test.com");

        mockEvent.getParticipants().add(new Registration(user1));
        mockEvent.getParticipants().add(new Registration(user2));

        // Simular waitlist
        Account waitUser = new Account("wait@test.com");
        Registration newRegister=new Registration();
        newRegister.setEvent(mockEvent);
        newRegister.setUser(waitUser);
        mockEvent.getWaitList().add(newRegister);
    }

    @Test
    void testGetRegistrationsFromEvent() {
        UUID eventId = mockEvent.getId();
        when(eventService.getEvent(eventId)).thenReturn(mockEvent);

        List<Registration> result = organizerService.getRegistrationsFromEvent(eventId);

        assertEquals(2, result.size());
        assertEquals("user1@test.com", result.get(0).getUser().getUsername());
        assertEquals("user2@test.com", result.get(1).getUser().getUsername());
    }

    @Test
    void testGetWaitlistFromEvent() {
        UUID eventId = mockEvent.getId();
        when(eventService.getEvent(eventId)).thenReturn(mockEvent);

        Queue<Registration> result = organizerService.getWaitlistFromEvent(eventId);

        assertEquals(1, result.size());
        assert result.peek() != null;
        assertEquals("wait@test.com", result. peek().getUser().getUsername());
    }

    @Test
    void testCloseRegistrations() {
        UUID eventId = mockEvent.getId();
        when(eventService.getEvent(eventId)).thenReturn(mockEvent);

        organizerService.closeRegistrations(eventId);

        assertEquals(EventState.EVENT_CLOSED , mockEvent.getEventState());
    }
    @Test
    void testRegisterParticipantAfterClosingRegistrations() {
        UUID eventId = mockEvent.getId();
        when(eventService.getEvent(eventId)).thenReturn(mockEvent);

        // Cerrar inscripciones
        organizerService.closeRegistrations(eventId);

        // Intentar registrar un nuevo participante después de cerrar las inscripciones
        Account newUser = new Account("newuser@test.com");
        Registration newRegistration = new Registration(newUser);

        // Intentar registrar el nuevo participante
        String result = mockEvent.registerParticipant(newRegistration);

        // Verificar que el resultado es "CERRADO" y que no se agregó al participante
        assertEquals(EventState.EVENT_CLOSED.toString(), result);
        assertFalse(mockEvent.getParticipants().contains(newRegistration));  // Verificar que no fue agregado
    }
}
