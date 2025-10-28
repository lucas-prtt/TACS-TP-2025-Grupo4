package ServiceTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic; // Necesario para mockStatic
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import com.mongodb.client.result.UpdateResult;

// Tus clases y DTOs
import org.DTOs.events.EventDTO;
import org.model.enums.EventState;
import org.model.enums.RegistrationState;
import org.model.events.Event;
import org.model.events.Registration;
import org.model.accounts.Account;
import org.repositories.EventRepository;
import org.repositories.RegistrationRepository;
import org.services.EventService;

// Tus excepciones
import org.exceptions.*;
import org.apache.coyote.BadRequestException;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

// Clase estática a mockear
import org.utils.SecurityUtils;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

  // 1. Mocks para todas las dependencias
  @Mock private EventRepository eventRepository;
  @Mock private MongoTemplate mongoTemplate;
  @Mock private RegistrationRepository registrationRepository;

  // 2. Inyecta los mocks en el servicio
  @InjectMocks
  private EventService eventService;

  // --- Datos de prueba reutilizables ---
  private UUID eventId;
  private UUID organizerId;
  private Event mockEvent;
  private EventDTO mockPatchDTO;
  private Account mockOrganizer;

  @BeforeEach
  void setUp() {
    eventId = UUID.randomUUID();
    organizerId = UUID.randomUUID();

    // Configuración del organizador
    mockOrganizer = new Account();
    mockOrganizer.setId(organizerId);

    // Configuración del Evento base (usando constructor completo para evitar NPEs)
    mockEvent = new Event(
        "Test Event", "Desc", LocalDateTime.now().plusDays(5), 60, "Loc", 10, null,
        BigDecimal.ZERO, null, null, mockOrganizer, null
    );
    mockEvent.setId(eventId);
    mockEvent.setEventState(EventState.EVENT_OPEN);
    mockEvent.setAvailableSeats(5);
    mockEvent.setParticipants(new ArrayList<>());
    mockEvent.setWaitList(new ArrayList<>());

    // DTO básico para el Patch
    mockPatchDTO = new EventDTO();
    mockPatchDTO.setTitle("Nuevo Título");

    // Simulamos la lectura inicial del evento (común a casi todos los tests)
    when(eventRepository.findById(eventId)).thenReturn(Optional.of(mockEvent));
  }

  // --- TEST 1: TRANSICIÓN EXITOSA DE OPEN a CANCELLED (Método patchEvent) ---
  @Test
  void patchEvent_shouldCancelEvent_whenStateIsCancelled() throws BadRequestException {
    // ARRANGE
    mockPatchDTO.setState(EventState.EVENT_CANCELLED);

    // Simular que el organizador es el usuario autenticado
    try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentAccountId).thenReturn(organizerId);

      // ACT
      eventService.patchEvent(eventId, mockPatchDTO);

      // ASSERT
      // 1. Verificar el estado del objeto en memoria
      assertEquals(EventState.EVENT_CANCELLED, mockEvent.getEventState());

      // 2. Verificar que se guardó el cambio de estado y los datos del patch
      verify(eventRepository, times(1)).save(mockEvent);

      // 3. Verificar las llamadas ATÓMICAS (handleCancelEvent)
      // Se debe haber llamado a updateMulti para cambiar el estado de las Registrations
      verify(mongoTemplate, times(1)).updateMulti(
          any(Query.class), any(Update.class), eq(Registration.class)
      );

      // Nota: Aquí no verificamos updateFirst porque la lógica fue cambiada para mutar y guardar al final
    }
  }

  @Test
  void patchEvent_shouldCloseAndCleanWaitlist_whenTransitioningToClosed() throws BadRequestException {
    // ARRANGE
    // 1. Estado inicial: Abierto (OPEN)
    mockEvent.setEventState(EventState.EVENT_OPEN);

    // 2. Simular que hay personas en la lista de espera para que la limpieza se ejecute
    mockEvent.getWaitList().add(new Registration());

    // DTO que pide cerrar el evento
    EventDTO patchDTO = new EventDTO();
    patchDTO.setState(EventState.EVENT_CLOSED);

    when(eventRepository.findById(eventId)).thenReturn(Optional.of(mockEvent));

    // Simular que el organizador es el usuario autenticado (requerido para pasar la seguridad)
    try (MockedStatic<org.utils.SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(org.utils.SecurityUtils.class)) {
      mockedSecurityUtils.when(org.utils.SecurityUtils::getCurrentAccountId).thenReturn(organizerId);

      // --- ACT ---
      eventService.patchEvent(eventId, patchDTO);

      // --- ASSERT ---

      // 1. Verificar que el estado final es CLOSED (se guarda en memoria)
      assertEquals(EventState.EVENT_CLOSED, mockEvent.getEventState());

      // 2. Verificar que se llamó a updateMulti para limpiar la waitlist
      //    (Esto confirma que handleCloseEvent fue ejecutado)
      verify(mongoTemplate, times(1)).updateMulti(
          any(Query.class),
          any(Update.class),
          eq(Registration.class) // Clase correcta sobre la que se aplica el updateMulti
      );

      // 3. Verificar que se guardó el cambio de estado final
      verify(eventRepository, times(1)).save(mockEvent);
    }
  }


  // --- TEST 3: VALIDACIÓN DE TIEMPO (Debe fallar si ya pasó la fecha) ---
  @Test
  void patchEvent_shouldThrowException_whenEventTimeHasPassed() {
    // ARRANGE
    // Configuramos el evento para que haya terminado en el pasado
    mockEvent.setStartDateTime(LocalDateTime.now().minusDays(2));
    mockEvent.setDurationMinutes(60); // Terminó ayer
    mockEvent.setEventState(EventState.EVENT_OPEN);

    // El DTO solo intenta modificar un campo de datos
    EventDTO patchDTO = new EventDTO();
    patchDTO.setTitle("Nuevo Título");

    try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentAccountId).thenReturn(organizerId);

      // ACT & ASSERT
      assertThrows(BadRequestException.class, () -> {
        eventService.patchEvent(eventId, patchDTO);
      });

      // Verificar que NO se hizo el save
      verify(eventRepository, never()).save(any());
      // Verificar que NO se intentó ninguna operación atómica
      verify(mongoTemplate, never()).updateMulti(
          any(Query.class),
          any(Update.class),
          any(Class.class) // <-- Esto resuelve la ambigüedad para updateMulti
      );
    }
  }

  // --- TEST 4: TRANSICIÓN INVÁLIDA ---
  @Test
  void patchEvent_shouldThrowException_whenTransitionIsInvalid() {
    // ARRANGE
    mockEvent.setEventState(EventState.EVENT_CANCELLED); // Estado CANCELLED

    EventDTO patchDTO = new EventDTO();
    patchDTO.setState(EventState.EVENT_OPEN); // Intento de volver a OPEN (Transición inválida)

    try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentAccountId).thenReturn(organizerId);

      // ACT & ASSERT
      assertThrows(BadRequestException.class, () -> {
        eventService.patchEvent(eventId, patchDTO);
      });

      // Verificar que NO hubo ninguna operación
      verify(eventRepository, never()).save(any());
      verify(mongoTemplate, never()).updateMulti(
          any(Query.class),
          any(Update.class),
          any(Class.class) // <-- Esto resuelve la ambigüedad para updateMulti
      );
    }
  }
}