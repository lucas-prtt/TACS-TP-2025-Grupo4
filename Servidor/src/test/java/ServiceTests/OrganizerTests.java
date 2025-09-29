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
    organizer = new Account();
    organizer.setUsername("organizer@test.com");
    organizer.setPassword("pwd-org");
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
    Account user1 = new Account();
    user1.setUsername("user1@test.com");
    user1.setPassword("pwd1fsadfasd");
    Account user2 = new Account();
    user2.setUsername("user2@test.com");
    user2.setPassword("pwd2sdfasfd");

    Registration r1 = new Registration(user1);
    r1.setState(RegistrationState.CONFIRMED);

    Registration r2 = new Registration(user2);
    r2.setState(RegistrationState.CONFIRMED);

    mockEvent.getParticipants().add(r1);
    mockEvent.getParticipants().add(r2);

    // Simular waitlist
    Account waitUser = new Account();
    waitUser.setUsername("wait@test.com");
    waitUser.setPassword("pwd-w");
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
    System.out.println(eventService.getAllEvents());
    List<Registration> result = organizerService.getRegistrationsFromEvent(
            mockEvent.getId(), RegistrationState.CONFIRMED, null, null
    );

    assertEquals(2, result.size());
    assertEquals("user1@test.com", result.get(0).getUser().getUsername());
    assertEquals("user2@test.com", result.get(1).getUser().getUsername());
  }

  @Test
  void testGetWaitlistFromEvent() {
    List<Registration> result = organizerService.getWaitlistFromEvent(
        organizer.getId(), mockEvent.getId()
    );

    assertEquals(1, result.size());
    assertNotNull(result.getFirst());
    assertEquals("wait@test.com", result.getFirst().getUser().getUsername());
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
    Account newUser = new Account();
    newUser.setUsername("newuser@test.com");
    newUser.setPassword("pwd-new");
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
