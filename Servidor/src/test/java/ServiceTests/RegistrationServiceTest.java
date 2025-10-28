package ServiceTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.model.enums.EventState;
import org.model.enums.RegistrationState;
import org.services.RegistrationService;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import com.mongodb.client.result.UpdateResult;
import org.springframework.dao.DuplicateKeyException;

// Tus clases
import org.model.events.Event;
import org.model.events.Registration;
import org.model.accounts.Account;
import org.repositories.EventRepository;
import org.repositories.AccountRepository;
import org.repositories.RegistrationRepository;

// Tus excepciones
import org.exceptions.*;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList; // ✅ CORRECCIÓN: Necesario para setear listas vacías

// Imports estáticos para Mockito
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

// ✅ CORRECCIÓN: Añadimos la configuración de Mockito para silenciar los "UnnecessaryStubbingException"
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // ✅ CORRECCIÓN: Silenciamos errores de stubs innecesarios
class RegistrationServiceTest {

  // 1. Mocks para todas las dependencias
  @Mock
  private RegistrationRepository registrationRepository;
  @Mock
  private EventRepository eventRepository;
  @Mock
  private AccountRepository accountRepository;
  @Mock
  private MongoTemplate mongoTemplate;

  // 2. Inyecta los mocks en el servicio
  @InjectMocks
  private RegistrationService registrationService;

  // --- Datos de prueba reutilizables ---
  private Event mockEvent;
  private Account mockAccount;
  private UUID eventId;
  private UUID accountId;

  @BeforeEach
  void setUp() {
    // Configuramos objetos base para las pruebas
    eventId = UUID.randomUUID();
    accountId = UUID.randomUUID();

    mockEvent = new Event(); // Este es un objeto REAL que usamos como dato
    mockEvent.setId(eventId);
    mockEvent.setEventState(EventState.EVENT_OPEN);
    mockEvent.setOrganizer(new Account());

    // ✅ CORRECCIÓN: Seteamos el estado real del objeto, en lugar de mockear los getters
    mockEvent.setParticipants(new ArrayList<>());
    mockEvent.setWaitList(new ArrayList<>());

    mockAccount = new Account();
    mockAccount.setId(accountId);

    // Simulamos las lecturas iniciales (ahora no dará error de UnnecessaryStubbing)
    when(eventRepository.findById(eventId)).thenReturn(Optional.of(mockEvent));
    when(accountRepository.findById(accountId)).thenReturn(Optional.of(mockAccount));
  }

  @Test
  void registerParticipantToEvent_shouldSucceed_whenCupoIsAvailable() {
    // --- 1. ARRANGE (Organizar) ---

    // ✅ CORRECCIÓN: Eliminamos los 'when(mockEvent...)' porque el estado ya está en el objeto real
    // when(mockEvent.getParticipants()).thenReturn(List.of()); // <-- BORRADO
    // when(mockEvent.getWaitList()).thenReturn(List.of()); // <-- BORRADO

    // Simulamos que el 'save' de reserva funciona
    when(registrationRepository.save(any(Registration.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // ¡LA CLAVE! Simulamos la operación atómica de cupo
    UpdateResult mockResult = mock(UpdateResult.class);
    when(mockResult.getModifiedCount()).thenReturn(1L); // SÍ había cupo

    when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(Event.class)))
        .thenReturn(mockResult);

    // --- 2. ACT (Actuar) ---
    Registration registration = registrationService.registerParticipantToEvent(eventId, accountId);

    // --- 3. ASSERT (Verificar) ---
    assertNotNull(registration);
    assertEquals(RegistrationState.CONFIRMED, registration.getCurrentState()); // Asumiendo que tu setter se llama 'setCurrentState'

    verify(registrationRepository, times(2)).save(registration);
    verify(accountRepository, times(1)).save(mockAccount);
    verify(mongoTemplate, times(1)).updateFirst(any(Query.class), any(Update.class), eq(Event.class));
  }

  @Test
  void registerParticipantToEvent_shouldGoToWaitlist_whenNoCupo() {
    // --- 1. ARRANGE (Organizar) ---
    // (No necesitas los 'when(mockEvent...)' porque el estado está en el objeto)
    when(registrationRepository.save(any(Registration.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // ¡LA CLAVE! Simulamos que NO había cupo
    UpdateResult mockResultCupo = mock(UpdateResult.class);
    when(mockResultCupo.getModifiedCount()).thenReturn(0L); // 0 significa que no había cupo

    // Simulamos un resultado para la segunda llamada (la de la waitlist)
    UpdateResult mockResultWaitlist = mock(UpdateResult.class);
    // (No necesitamos mockear su getModifiedCount(), solo necesitamos el objeto)

    // ✅ --- CORRECCIÓN ---
    // En lugar de dos 'when' complejos con 'argThat',
    // usamos UNO SOLO y encadenamos las respuestas.
    when(mongoTemplate.updateFirst(
        any(Query.class),
        any(Update.class),
        eq(Event.class))
    )
        .thenReturn(mockResultCupo)     // 1. La PRIMERA llamada (la del cupo) devolverá esto.
        .thenReturn(mockResultWaitlist); // 2. La SEGUNDA llamada (la de la waitlist) devolverá esto.
    // ----------------------

    // --- 2. ACT (Actuar) ---
    Registration registration = registrationService.registerParticipantToEvent(eventId, accountId);

    // --- 3. ASSERT (Verificar) ---
    assertNotNull(registration);
    // ✅ CORRECCIÓN: Usamos tu setter personalizado 'setState'
    assertEquals(RegistrationState.WAITLIST, registration.getCurrentState());

    // Verificamos las llamadas:
    verify(registrationRepository, times(2)).save(registration);
    verify(accountRepository, times(1)).save(mockAccount);
    // Se llamó 2 veces a updateFirst: 1 para cupo (falló), 1 para waitlist (éxito)
    verify(mongoTemplate, times(2)).updateFirst(any(Query.class), any(Update.class), eq(Event.class));
  }

  @Test
  void registerParticipantToEvent_shouldThrowException_whenRaceConditionOnSave() {
    // --- 1. ARRANGE (Organizar) ---
    // ✅ CORRECCIÓN: Eliminamos los 'when(mockEvent...)'
    // when(mockEvent.getParticipants()).thenReturn(List.of()); // <-- BORRADO
    // when(mockEvent.getWaitList()).thenReturn(List.of()); // <-- BORRADO

    // ¡LA CLAVE! Simulamos el fallo del Índice Único en la primera "reserva"
    when(registrationRepository.save(any(Registration.class)))
        .thenThrow(new DuplicateKeyException("Índice único falló"));

    // --- 2. ACT & 3. ASSERT ---
    assertThrows(AlreadyRegisteredException.class, () -> {
      registrationService.registerParticipantToEvent(eventId, accountId);
    });

    // Verificamos que se detuvo rápido
    verify(mongoTemplate, never()).updateFirst(
        any(Query.class),
        any(Update.class),
        any(Class.class) // Correcto
    );
    verify(accountRepository, never()).save(any());
  }

  @Test
  void cancelRegistration_shouldPromoteFromWaitlist_whenParticipantCancels() {
    // --- 1. ARRANGE (Organizar) ---
    // ... (Tu código de Arrange sigue igual) ...
    Registration registrationToCancel = new Registration(mockEvent, mockAccount, RegistrationState.CONFIRMED);
    UUID registrationIdToCancel = registrationToCancel.getId();
    when(registrationRepository.findById(registrationIdToCancel)).thenReturn(Optional.of(registrationToCancel));

    Account accountToPromote = new Account(); accountToPromote.setId(UUID.randomUUID());
    Registration registrationToPromote = new Registration(mockEvent, accountToPromote, RegistrationState.WAITLIST);
    registrationToPromote.setId(UUID.randomUUID());

    Event mockEventBeforePop = new Event();
    mockEventBeforePop.setId(eventId);
    mockEventBeforePop.setWaitList(new ArrayList<>(List.of(registrationToPromote)));

    when(mongoTemplate.findAndModify(
        any(Query.class),
        any(Update.class),
        any(FindAndModifyOptions.class),
        eq(Event.class))
    ).thenReturn(mockEventBeforePop);

    // --- 2. ACT (Actuar) ---
    registrationService.cancelRegistration(registrationIdToCancel);

    // --- 3. ASSERT (Verificar) ---
    assertEquals(RegistrationState.CANCELED, registrationToCancel.getCurrentState());
    verify(registrationRepository).save(registrationToCancel);

    assertEquals(RegistrationState.CONFIRMED, registrationToPromote.getCurrentState());
    verify(registrationRepository).save(registrationToPromote);

    // ✅ --- CORRECCIÓN: Verificar las DOS llamadas separadas ---
    // 1. Verificar que se llamó a updateFirst con $pull
    verify(mongoTemplate, times(1)).updateFirst( // times(1) es opcional pero claro
        any(Query.class),
        argThat(update -> update.getUpdateObject().containsKey("$pull") &&
            update.getUpdateObject().get("$pull", org.bson.Document.class).containsKey("participants")), // Verifica $pull en participants
        eq(Event.class)
    );

    // 2. Verificar que se llamó a updateFirst con $push
    verify(mongoTemplate, times(1)).updateFirst( // times(1) es opcional pero claro
        any(Query.class),
        argThat(update -> update.getUpdateObject().containsKey("$push") &&
            update.getUpdateObject().get("$push", org.bson.Document.class).containsKey("participants")), // Verifica $push en participants
        eq(Event.class)
    );

  }

  @Test
  void cancelRegistration_shouldFreeUpSeat_whenWaitlistIsEmpty() {
    // --- 1. ARRANGE (Organizar) ---
    Registration registrationToCancel = new Registration(mockEvent, mockAccount, RegistrationState.CONFIRMED);
    // ✅ IMPORTANT: Need the ID for the findById mock
    UUID registrationIdToCancel = registrationToCancel.getId();

    // ✅ --- CORRECTION: Mock the findById call ---
    // Tell the repository to return our object when asked for its ID
    when(registrationRepository.findById(registrationIdToCancel))
        .thenReturn(Optional.of(registrationToCancel));
    // ---------------------------------------------

    Event mockEventBeforePop = new Event();
    mockEventBeforePop.setId(eventId);
    mockEventBeforePop.setWaitList(new ArrayList<>());

    when(mongoTemplate.findAndModify(
        any(Query.class),
        any(Update.class),
        any(FindAndModifyOptions.class),
        eq(Event.class))
    ).thenReturn(mockEventBeforePop);

    // --- 2. ACT (Actuar) ---
    // Pass the ID, as per your updated method signature
    registrationService.cancelRegistration(registrationIdToCancel);

    // --- 3. ASSERT (Verificar) ---
    // Verify the save AFTER the state is changed inside the method
    verify(registrationRepository).save(registrationToCancel);

    // This verification might now be redundant if the above one works,
    // but it doesn't hurt unless it causes issues.
    // Let's keep it for now to ensure only ONE save happens.
    verify(registrationRepository, times(1)).save(any(Registration.class));

    verify(mongoTemplate).updateFirst(
        any(Query.class),
        argThat(update -> update.getUpdateObject().containsKey("$inc") &&
            update.getUpdateObject().containsKey("$pull")),
        eq(Event.class)
    );
  }


}