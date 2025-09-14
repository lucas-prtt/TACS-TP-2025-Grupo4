package ServiceTests;

import org.exceptions.EventRegistrationsClosedException;
import org.model.enums.EventState;
import org.model.enums.RegistrationState;
import org.model.events.Event;
import org.model.events.Registration;
import org.model.accounts.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.model.accounts.Account;
import org.model.enums.EventState;
import org.model.events.Event;
import org.model.events.Registration;
import org.services.EventService;
import org.services.OrganizerService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Queue;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class OrganizerTests {


  private EventService eventService;
  private OrganizerService organizerService;
  private Event mockEvent;
  private Account organizer;

  @BeforeEach
  void setUp() {
    // Mock del EventService
    eventService = Mockito.mock(EventService.class);
    organizerService = new OrganizerService(eventService);

    // Crear organizer (usa el constructor (username, password) según tu modelo)
    organizer = new Account("organizer@test.com", "pwd-org");

    // Crear evento real (constructor que setea id internamente)
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

    // asegurarnos que el id esté seteado
    assertNotNull(mockEvent.getId(), "El constructor del Event debería inicializar un UUID en id");

    // Simular participantes
    Account user1 = new Account("user1@test.com", "pwd1fsadfasd");
    Account user2 = new Account("user2@test.com", "pwd2sdfasfd");

    mockEvent.getParticipants().add(new Registration(user1));
    mockEvent.getParticipants().add(new Registration(user2));

    // Simular waitlist
    Account waitUser = new Account("wait@test.com", "pwd-w");
    Registration newRegister = new Registration();
    newRegister.setEvent(mockEvent);
    newRegister.setUser(waitUser);
    mockEvent.getWaitList().add(newRegister);

    // Stub: cuando EventService pida el evento por su id, devolver mockEvent
    // Atención: OrganizerService llama eventService.getEvent(eventId) (solo con eventId)
    when(eventService.getEvent(mockEvent.getId())).thenReturn(mockEvent);
  }

  @Test
  void testGetRegistrationsFromEvent() {
    // Llamar con accountId = organizer.getId() y eventId = mockEvent.getId()
    List<Registration> result = organizerService.getRegistrationsFromEvent(
            mockEvent.getId(), RegistrationState.CONFIRMED, null, null
    );

    assertEquals(2, result.size());
    assertEquals("user1@test.com", result.get(0).getUser().getUsername());
    assertEquals("user2@test.com", result.get(1).getUser().getUsername());
  }

  @Test
  void testGetWaitlistFromEvent() {
    Queue<Registration> result = organizerService.getWaitlistFromEvent(
        organizer.getId(), mockEvent.getId()
    );

    assertEquals(1, result.size());
    assertNotNull(result.peek());
    assertEquals("wait@test.com", result.peek().getUser().getUsername());
  }

  @Test
  void testCloseRegistrations() {
    organizerService.closeRegistrations(organizer.getId(), mockEvent.getId());

    assertEquals(EventState.EVENT_CLOSED, mockEvent.getEventState());
  }

  @Test
  void testRegisterParticipantAfterClosingRegistrations() {
    // Cerrar inscripciones usando el servicio (esto cambia el estado del evento)
    organizerService.closeRegistrations(organizer.getId(), mockEvent.getId());

    // Intentar registrar un nuevo participante después de cerrar las inscripciones
    Account newUser = new Account("newuser@test.com", "pwd-new");
    Registration newRegistration = new Registration(newUser);

        // Intentar registrar el nuevo participante
        assertThrows(EventRegistrationsClosedException.class, () -> {
            mockEvent.registerParticipant(newRegistration);
        });



        // Verificar que el resultado es "CERRADO" y que no se agregó al participante
        assertEquals(EventState.EVENT_CLOSED, mockEvent.getEventState());
        assertFalse(mockEvent.getParticipants().contains(newRegistration));  // Verificar que no fue agregado
    }
}
