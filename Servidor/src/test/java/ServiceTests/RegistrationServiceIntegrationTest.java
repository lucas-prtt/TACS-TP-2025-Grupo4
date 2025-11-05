//package ServiceTests; // O donde vivan tus tests
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.CyclicBarrier;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Test;
//import org.model.enums.EventState;
//import org.model.enums.RegistrationState;
//import org.model.events.Registration;
//import org.servidor.Servidor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.MongoDBContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//// Excepciones y modelos
//import org.exceptions.AlreadyRegisteredException;
//import org.model.events.Event;
//import org.model.accounts.Account;
//// Repositorios (para limpiar)
//import org.repositories.EventRepository;
//import org.repositories.AccountRepository;
//import org.repositories.RegistrationRepository;
//// El Servicio que probamos
//import org.services.RegistrationService;
//
//import java.util.UUID;
//import static org.junit.jupiter.api.Assertions.*;
//
//@Testcontainers // 1. Activa Testcontainers
//@SpringBootTest(classes = Servidor.class)  // 2. Carga el contexto completo de Spring (para @Transactional)
//class RegistrationServiceIntegrationTest { // DOCKER DESKTOP RUNNING
//
//  // 3. Define el contenedor de Mongo (versión 7)
//  //    y le dice que se inicie como un replica set "rs0".
//  @Container
//  static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7")
//      .withCommand("--replSet", "rs0");
//
//  /**
//   * 4. "Engancha" la configuración de Spring
//   * Le dice a Spring que use la URL de nuestro contenedor.
//   */
//  @DynamicPropertySource
//  static void setProperties(DynamicPropertyRegistry registry) {
//    // Iniciamos el contenedor manualmente
//    // (Esto es necesario para poder inicializar el replica set)
//    if (!mongoDBContainer.isRunning()) {
//      mongoDBContainer.start();
//    }
//
//    // 5. ¡CLAVE! Inicializamos el Replica Set (el 'rs.initiate()')
//    //    Esto es necesario para que @Transactional funcione.
//    try {
//      mongoDBContainer.execInContainer("mongosh", "--eval",
//          "rs.initiate({ _id: 'rs0', members: [{ _id: 0, host: 'localhost:27017' }] })");
//    } catch (Exception e) {
//      // A veces falla la primera vez si el contenedor no está listo,
//      // un reintento simple puede ayudar.
//      try { Thread.sleep(1000); } catch (InterruptedException ie) {}
//      try {
//        mongoDBContainer.execInContainer("mongosh", "--eval",
//            "rs.initiate({ _id: 'rs0', members: [{ _id: 0, host: 'localhost:27017' }] })");
//      } catch (Exception e2) {
//        // Si falla de nuevo, lanzamos el error
//        throw new RuntimeException("Fallo al inicializar el replica set", e2);
//      }
//    }
//
//    // 6. Le pasamos la URI de conexión a Spring
//    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
//  }
//
//  // Inyectamos el servicio real (no un mock)
//  @Autowired
//  private RegistrationService registrationService;
//
//  // Inyectamos los repositorios para verificar la BD
//  @Autowired
//  private EventRepository eventRepository;
//  @Autowired
//  private AccountRepository accountRepository;
//  @Autowired
//  private RegistrationRepository registrationRepository;
//
//  /**
//   * 7. Limpiamos la BD después de CADA prueba
//   * para que los tests no interfieran entre sí.
//   */
//  @AfterEach
//  void tearDown() {
//    registrationRepository.deleteAll();
//    eventRepository.deleteAll();
//    accountRepository.deleteAll();
//  }
//
//  @Test
//  void testTransactionalRollback_OnDuplicateKeyException() {
//    // --- 1. ARRANGE (Organizar) ---
//    // Creamos un organizador (puede ser un usuario simple)
//    Account organizer = new Account();
//    organizer.setId(UUID.randomUUID());
//    accountRepository.save(organizer); // Guardamos el organizador
//
//    Event event = new Event(/*... tus datos ...*/);
//    event.setId(UUID.randomUUID());
//    event.setAvailableSeats(10);
//    event.setEventState(EventState.EVENT_OPEN);
//    event.setOrganizer(organizer); // <-- ✅ SOLUCIÓN: Asigna el organizador
//    eventRepository.save(event);
//    UUID eventId = event.getId();
//
//    Account participant = new Account(/*... tus datos ...*/);
//    participant.setId(UUID.randomUUID());
//    accountRepository.save(participant);
//    UUID accountId = participant.getId();
//
//    // --- 2. ACT (Actuar) ---
//    Registration reg1 = registrationService.registerParticipantToEvent(eventId, accountId);
//    Exception exception = assertThrows(AlreadyRegisteredException.class, () -> {
//      registrationService.registerParticipantToEvent(eventId, accountId);
//    });
//
//    // --- 3. ASSERT (Verificar) ---
//    // (El resto del test sigue igual)
//    assertTrue(exception.getMessage().contains("Ya esta inscripto"));
//    Event finalEvent = eventRepository.findById(eventId).get();
//    assertEquals(9, finalEvent.getAvailableSeats());
//    assertEquals(1, finalEvent.getParticipants().size());
//    assertEquals(1, registrationRepository.count());
//  }
//
//  @Test
//  void testRegistration_FillsCupoAndThenWaitlist() {
//    // --- 1. ARRANGE (Organizar) ---
//    // Creamos un organizador
//    Account organizer = new Account();
//    organizer.setId(UUID.randomUUID());
//    accountRepository.save(organizer);
//
//    Event event = new Event(/*... tus datos ...*/);
//    event.setId(UUID.randomUUID());
//    event.setAvailableSeats(1);
//    event.setEventState(EventState.EVENT_OPEN);
//    event.setOrganizer(organizer); // <-- ✅ SOLUCIÓN: Asigna el organizador
//    eventRepository.save(event);
//    UUID eventId = event.getId();
//
//    // Creamos 3 usuarios participantes
//    Account userA = new Account();
//    userA.setId(UUID.randomUUID());
//    Account userB = new Account();
//    userB.setId(UUID.randomUUID());
//    Account userC = new Account();
//    userC.setId(UUID.randomUUID());
//    accountRepository.saveAll(List.of(userA, userB, userC));
//
//    // --- 2. ACT (Actuar) ---
//    // (El resto del test sigue igual)
//    Registration regA = registrationService.registerParticipantToEvent(eventId, userA.getId());
//    Registration regB = registrationService.registerParticipantToEvent(eventId, userB.getId());
//    Registration regC = registrationService.registerParticipantToEvent(eventId, userC.getId());
//
//    // --- 3. ASSERT (Verificar) ---
//    // (El resto del test sigue igual)
//    assertEquals(RegistrationState.CONFIRMED, regA.getCurrentState());
//    assertEquals(RegistrationState.WAITLIST, regB.getCurrentState());
//    assertEquals(RegistrationState.WAITLIST, regC.getCurrentState());
//    Event finalEvent = eventRepository.findById(eventId).get();
//    assertEquals(0, finalEvent.getAvailableSeats());
//    assertEquals(1, finalEvent.getParticipants().size());
//    assertEquals(2, finalEvent.getWaitList().size());
//  }
//
//
//  @Test
//  void testConcurrency_HandlesRaceConditionForSeatsAndWaitlist() throws InterruptedException {
//    // --- 1. ARRANGE (Organizar Datos) ---
//
//    // 1a. Crear un organizador (necesario para el evento)
//    Account organizer = new Account();
//    organizer.setId(UUID.randomUUID());
//    accountRepository.save(organizer);
//
//    // 1b. Crear el Evento con Cupos Limitados
//    int cuposDisponibles = 10; // Ejemplo: 10 asientos
//    Event event = new Event( // Usando tu constructor completo
//        "Evento Concurrido", "Descripción", LocalDateTime.now().plusDays(1),
//        120, "Lugar Test", cuposDisponibles, null, BigDecimal.ZERO,
//        null, null, organizer, null
//    );
//    event.setId(UUID.randomUUID());
//    event.setAvailableSeats(cuposDisponibles); // ¡Importante setearlo!
//    event.setEventState(EventState.EVENT_OPEN);
//    eventRepository.save(event);
//    UUID eventId = event.getId();
//
//    // 1c. Crear MÁS usuarios que cupos
//    int numeroDeUsuariosSimultaneos = 50; // Ejemplo: 50 usuarios intentando
//    List<Account> users = IntStream.range(0, numeroDeUsuariosSimultaneos)
//        .mapToObj(i -> {
//          Account user = new Account();
//          user.setId(UUID.randomUUID());
//          user.setUsername("user" + i); // Dales nombres únicos
//          return user;
//        })
//        .collect(Collectors.toList());
//    accountRepository.saveAll(users);
//    List<UUID> userIds = users.stream().map(Account::getId).collect(Collectors.toList());
//
//    // 1d. Preparar las herramientas de concurrencia
//    ExecutorService executor = Executors.newFixedThreadPool(numeroDeUsuariosSimultaneos); // Un hilo por usuario
//    CountDownLatch latch = new CountDownLatch(numeroDeUsuariosSimultaneos); // Contador para esperar a todos
//
//    // --- 2. ACT (Ejecutar en Paralelo) ---
//
//    // Lanzamos todas las inscripciones a la vez
//    userIds.forEach(userId -> {
//      executor.submit(() -> {
//        try {
//          // Cada hilo intenta inscribir a UN usuario diferente
//          registrationService.registerParticipantToEvent(eventId, userId);
//          System.out.println("Usuario " + userId + " procesado.");
//        } catch (Exception e) {
//          // ¡IMPORTANTE! Atrapamos las excepciones esperadas
//          // (Ej: AlreadyRegistered si @Retryable falla, NoSeatsAvailable, etc.)
//          // No queremos que el test falle por estas excepciones controladas.
//          System.err.println("Usuario " + userId + " falló (esperado si no hay cupo): " + e.getMessage());
//        } finally {
//          latch.countDown(); // Aseguramos que el contador baje incluso si hay error
//        }
//      });
//    });
//
//    // Esperamos MÁXIMO un tiempo razonable (ej. 30 segundos) a que todos terminen
//    boolean finishedInTime = latch.await(30, TimeUnit.SECONDS);
//    assertTrue(finishedInTime, "La prueba de concurrencia tardó demasiado.");
//    executor.shutdown(); // Cerramos el pool de hilos
//
//    // --- 3. ASSERT (Verificar el Estado Final) ---
//
//    // Recargamos el estado final del evento DESDE LA BASE DE DATOS
//    // Es crucial no usar el objeto 'event' que teníamos en memoria.
//    Event eventoFinal = eventRepository.findById(eventId).get();
//    long inscripcionesTotales = registrationRepository.countByEvent_Id(eventId);
//
//    // ¡LAS VERIFICACIONES DE ORO! 🏆
//    // Estas 4 líneas prueban que tu lógica manejó la concurrencia perfectamente.
//    assertEquals(0, eventoFinal.getAvailableSeats(), "Los cupos disponibles deben ser 0");
//    assertEquals(cuposDisponibles, eventoFinal.getParticipants().size(), "El número de participantes confirmados debe ser igual a los cupos iniciales");
//    assertEquals(numeroDeUsuariosSimultaneos - cuposDisponibles, eventoFinal.getWaitList().size(), "La lista de espera debe tener los usuarios restantes");
//    assertEquals(numeroDeUsuariosSimultaneos, inscripcionesTotales, "El número total de inscripciones (confirmadas + waitlist) debe ser igual al total de usuarios");
//
//    System.out.println("Prueba de concurrencia finalizada con éxito.");
//    System.out.println("Cupos finales: " + eventoFinal.getAvailableSeats());
//    System.out.println("Participantes confirmados: " + eventoFinal.getParticipants().size());
//    System.out.println("En lista de espera: " + eventoFinal.getWaitList().size());
//  }
//
//  @Test
//  void testConcurrency_MixedRegistrationsAndCancellations() throws InterruptedException {
//    // --- 1. ARRANGE (Organizar Datos Iniciales) ---
//
//    // 1a. Crear Organizador y Evento (pocos cupos)
//    Account organizer = new Account();
//    organizer.setId(UUID.randomUUID());
//    accountRepository.save(organizer);
//
//    int cuposIniciales = 3; // Evento pequeño para forzar la waitlist rápido
//    Event event = new Event("Evento Mixto", "Desc", LocalDateTime.now().plusDays(2), 60, "Lugar Mix", cuposIniciales, null, BigDecimal.TEN, null, null, organizer, null);
//    event.setId(UUID.randomUUID());
//    event.setAvailableSeats(cuposIniciales);
//    event.setEventState(EventState.EVENT_OPEN);
//    eventRepository.save(event);
//    UUID eventId = event.getId();
//
//    // 1b. Crear Usuarios
//    int totalUsuarios = 20;
//    List<Account> users = IntStream.range(0, totalUsuarios)
//        .mapToObj(i -> {
//          Account user = new Account();
//          user.setId(UUID.randomUUID());
//          user.setUsername("user" + i);
//          return user;
//        })
//        .collect(Collectors.toList());
//    accountRepository.saveAll(users);
//
//    // 1c. Llenar el Evento y Crear una Waitlist Inicial
//    int usuariosEnWaitlistInicial = 5;
//    List<Registration> initialRegistrations = new ArrayList<>();
//    for (int i = 0; i < cuposIniciales + usuariosEnWaitlistInicial; i++) {
//      // Usamos el servicio para asegurar el estado correcto (CONFIRMED o WAITLIST)
//      initialRegistrations.add(registrationService.registerParticipantToEvent(eventId, users.get(i).getId()));
//    }
//
//    // Verificación rápida del estado inicial (opcional pero útil)
//    Event eventAfterSetup = eventRepository.findById(eventId).get();
//    assertEquals(0, eventAfterSetup.getAvailableSeats(), "Setup fallido: Aún quedan cupos");
//    assertEquals(cuposIniciales, eventAfterSetup.getParticipants().size(), "Setup fallido: No se llenaron los participantes");
//    assertEquals(usuariosEnWaitlistInicial, eventAfterSetup.getWaitList().size(), "Setup fallido: No se llenó la waitlist");
//
//    // --- 2. PREPARAR LA CARRERA ---
//
//    // 2a. Usuarios que cancelarán (CONFIRMADOS)
//    int numeroDeCancelaciones = 2; // Ej: Los 2 primeros confirmados cancelan
//    List<Registration> usersToCancel = initialRegistrations.subList(0, numeroDeCancelaciones);
//
//    // 2b. Nuevos Usuarios que intentarán inscribirse
//    int numeroDeNuevasInscripciones = 7; // Ej: 7 nuevos usuarios
//    List<Account> usersToRegister = users.subList(
//        cuposIniciales + usuariosEnWaitlistInicial, // Empezar después de los ya inscritos/en espera
//        cuposIniciales + usuariosEnWaitlistInicial + numeroDeNuevasInscripciones
//    );
//
//    // 2c. Preparar Hilos y Contador
//    int totalTareasConcurrentes = numeroDeCancelaciones + numeroDeNuevasInscripciones;
//    ExecutorService executor = Executors.newFixedThreadPool(totalTareasConcurrentes);
//    CountDownLatch latch = new CountDownLatch(totalTareasConcurrentes);
//
//    // --- 3. ACT (Ejecutar la Carrera) ---
//
//    // 3a. Enviar tareas de cancelación
//    usersToCancel.forEach(registration -> {
//      executor.submit(() -> {
//        try {
//          registrationService.cancelRegistration(registration.getId());
//          System.out.println("Cancelación procesada para: " + registration.getUser().getId());
//        } catch (Exception e) {
//          System.err.println("Error inesperado en cancelación: " + e.getMessage());
//        } finally {
//          latch.countDown();
//        }
//      });
//    });
//
//    // 3b. Enviar tareas de nueva inscripción
//    usersToRegister.forEach(account -> {
//      executor.submit(() -> {
//        try {
//          registrationService.registerParticipantToEvent(eventId, account.getId());
//          System.out.println("Inscripción procesada para: " + account.getId());
//        } catch (Exception e) {
//          System.err.println("Error esperado en inscripción: " + e.getMessage());
//        } finally {
//          latch.countDown();
//        }
//      });
//    });
//
//    // 3c. Esperar a que todo termine
//    boolean finishedInTime = latch.await(45, TimeUnit.SECONDS); // Damos un poco más de tiempo
//    assertTrue(finishedInTime, "La prueba de concurrencia mixta tardó demasiado.");
//    executor.shutdown();
//
//    // --- 4. ASSERT (Verificar el Estado Final) ---
//
//    Event eventoFinal = eventRepository.findById(eventId).get();
//    List<Registration> finalParticipants = eventoFinal.getParticipants();
//    List<Registration> finalWaitlist = eventoFinal.getWaitList();
//    long totalActiveRegistrations = registrationRepository.countByEvent_IdAndCurrentStateNot(eventId, RegistrationState.CANCELED);
//
//    // IDs esperados (para verificación más profunda)
//    List<UUID> expectedPromotedIds = initialRegistrations.subList(cuposIniciales, cuposIniciales + numeroDeCancelaciones) // Los primeros X de la waitlist original
//        .stream().map(r -> r.getUser().getId()).collect(Collectors.toList());
//    List<UUID> expectedRemainingOriginalWaitlistIds = initialRegistrations.subList(cuposIniciales + numeroDeCancelaciones, cuposIniciales + usuariosEnWaitlistInicial)
//        .stream().map(r -> r.getUser().getId()).collect(Collectors.toList());
//    List<UUID> expectedNewWaitlistIds = usersToRegister.stream().map(Account::getId).collect(Collectors.toList());
//    List<UUID> expectedFinalParticipantIds = new ArrayList<>(initialRegistrations.subList(numeroDeCancelaciones, cuposIniciales) // Los confirmados originales que NO cancelaron
//        .stream().map(r -> r.getUser().getId()).toList());
//    expectedFinalParticipantIds.addAll(expectedPromotedIds); // Añadir los promovidos
//
//    // --- ¡LAS VERIFICACIONES DE ORO! 🏆 ---
//    assertEquals(0, eventoFinal.getAvailableSeats(), "Cupos disponibles deben ser 0");
//    assertEquals(cuposIniciales, finalParticipants.size(), "Número final de participantes confirmados incorrecto");
//    assertEquals(usuariosEnWaitlistInicial - numeroDeCancelaciones + numeroDeNuevasInscripciones, finalWaitlist.size(), "Número final en waitlist incorrecto");
//    assertEquals(cuposIniciales + usuariosEnWaitlistInicial - numeroDeCancelaciones + numeroDeNuevasInscripciones, totalActiveRegistrations, "Número total de inscripciones activas incorrecto");
//
//    // Verificación más detallada de QUIÉN quedó en cada lista
//    List<UUID> actualParticipantIds = finalParticipants.stream().map(r -> r.getUser().getId()).collect(Collectors.toList());
//    List<UUID> actualWaitlistIds = finalWaitlist.stream().map(r -> r.getUser().getId()).collect(Collectors.toList());
//
//    // Comparamos los IDs (ignorando el orden dentro de participants, pero manteniendo el orden en waitlist si es importante)
//    assertTrue(actualParticipantIds.containsAll(expectedFinalParticipantIds) && expectedFinalParticipantIds.containsAll(actualParticipantIds),
//        "Los participantes finales no son los esperados");
//    assertEquals(expectedRemainingOriginalWaitlistIds, actualWaitlistIds.subList(0, expectedRemainingOriginalWaitlistIds.size()),
//        "La primera parte de la waitlist (originales restantes) no es la esperada");
//    // Convertimos a Set para comparar la segunda parte (nuevos) sin importar el orden exacto en que llegaron
//    assertEquals(expectedNewWaitlistIds.size(), actualWaitlistIds.subList(expectedRemainingOriginalWaitlistIds.size(), actualWaitlistIds.size()).size(),
//        "El número de nuevos en waitlist no coincide");
//    assertTrue(actualWaitlistIds.subList(expectedRemainingOriginalWaitlistIds.size(), actualWaitlistIds.size()).containsAll(expectedNewWaitlistIds),
//        "Los nuevos usuarios en la waitlist no son los esperados");
//
//
//    System.out.println("Prueba de concurrencia MIXTA finalizada con éxito.");
//    System.out.println("Cupos finales: " + eventoFinal.getAvailableSeats());
//    System.out.println("Participantes confirmados: " + finalParticipants.size());
//    System.out.println("En lista de espera: " + finalWaitlist.size());
//  }
//
//
//  @Test
//  void testConcurrency_CancelConfirmedVsCancelWaitlistLeader() throws Exception { // Puede lanzar excepciones de concurrencia
//    // --- 1. ARRANGE (Organizar Datos Iniciales) ---
//
//    // 1a. Organizador y Evento (1 cupo)
//    Account organizer = new Account(); organizer.setId(UUID.randomUUID()); accountRepository.save(organizer);
//    int cupos = 1;
//    Event event = new Event("Evento Colisión", "Desc", LocalDateTime.now().plusDays(3), 60, "Lugar Col", cupos, null, BigDecimal.ONE, null, null, organizer, null);
//    event.setId(UUID.randomUUID()); event.setAvailableSeats(cupos); event.setEventState(EventState.EVENT_OPEN);
//    eventRepository.save(event);
//    UUID eventId = event.getId();
//
//    // 1b. Crear Usuarios: A (Confirmado), C (Waitlist #1), D (Waitlist #2)
//    Account userA = new Account(); userA.setId(UUID.randomUUID()); userA.setUsername("UserA_Confirmed");
//    Account userC = new Account(); userC.setId(UUID.randomUUID()); userC.setUsername("UserC_Waitlist1");
//    Account userD = new Account(); userD.setId(UUID.randomUUID()); userD.setUsername("UserD_Waitlist2");
//    accountRepository.saveAll(List.of(userA, userC, userD));
//
//    // 1c. Inscribirlos para establecer el estado inicial
//    Registration regA = registrationService.registerParticipantToEvent(eventId, userA.getId()); // Ocupa el cupo
//    Registration regC = registrationService.registerParticipantToEvent(eventId, userC.getId()); // Waitlist #1
//    Registration regD = registrationService.registerParticipantToEvent(eventId, userD.getId()); // Waitlist #2
//
//    // Verificación rápida del estado inicial
//    Event eventAfterSetup = eventRepository.findById(eventId).get();
//    assertEquals(0, eventAfterSetup.getAvailableSeats());
//    assertEquals(1, eventAfterSetup.getParticipants().size()); // UserA
//    assertEquals(2, eventAfterSetup.getWaitList().size());    // UserC, UserD
//    assertEquals(RegistrationState.CONFIRMED, registrationRepository.findById(regA.getId()).get().getCurrentState());
//    assertEquals(RegistrationState.WAITLIST, registrationRepository.findById(regC.getId()).get().getCurrentState());
//    assertEquals(RegistrationState.WAITLIST, registrationRepository.findById(regD.getId()).get().getCurrentState());
//
//    // --- 2. PREPARAR LA CARRERA ---
//
//    // Usaremos una barrera para sincronizar los hilos JUSTO antes de la colisión
//    final CyclicBarrier barrier = new CyclicBarrier(2);
//    // Usaremos un latch para esperar que ambos hilos terminen
//    final CountDownLatch endLatch = new CountDownLatch(2);
//    // Usaremos un ExecutorService
//    ExecutorService executor = Executors.newFixedThreadPool(2);
//
//    // --- 3. ACT (Ejecutar la Carrera) ---
//
//    // Hilo 1: Cancela al Confirmado (UserA), intentará promover a UserC
//    executor.submit(() -> {
//      try {
//        // Simulamos llegar al punto crítico (justo antes de modificar el Event)
//        // En un test real más avanzado, usaríamos AspectJ o mocks con delays,
//        // aquí simulamos con un punto de espera simple antes de llamar al servicio.
//        System.out.println("Hilo A (Cancela Confirmado) listo para colisionar...");
//        barrier.await(10, TimeUnit.SECONDS); // Espera a Hilo C
//
//        // Ahora sí, llamamos al método
//        registrationService.cancelRegistration(regA.getId());
//        System.out.println("Hilo A (Cancela Confirmado) ejecutado.");
//
//      } catch (Exception e) {
//        System.err.println("Hilo A falló: " + e.getMessage());
//        // En esta prueba, una excepción podría ser un resultado válido si C cancela primero
//      } finally {
//        endLatch.countDown();
//      }
//    });
//
//    // Hilo 2: Cancela al Waitlist #1 (UserC)
//    executor.submit(() -> {
//      try {
//        // Esperamos a Hilo A en la barrera
//        System.out.println("Hilo C (Cancela Waitlist) listo para colisionar...");
//        barrier.await(10, TimeUnit.SECONDS);
//
//        // Llamamos al método
//        registrationService.cancelRegistration(regC.getId());
//        System.out.println("Hilo C (Cancela Waitlist) ejecutado.");
//
//      } catch (Exception e) {
//        System.err.println("Hilo C falló: " + e.getMessage());
//        // Podría fallar si A promueve a C antes de que C cancele
//      } finally {
//        endLatch.countDown();
//      }
//    });
//
//    // Esperamos a que ambos hilos terminen
//    boolean finishedInTime = endLatch.await(30, TimeUnit.SECONDS);
//    assertTrue(finishedInTime, "La prueba de colisión tardó demasiado.");
//    executor.shutdown();
//
//    // --- 4. ASSERT (Verificar el Estado Final) ---
//
//    // Recargamos TODO desde la BD
//    Event eventoFinal = eventRepository.findById(eventId).get();
//    Registration regAFinal = registrationRepository.findById(regA.getId()).get();
//    Registration regCFinal = registrationRepository.findById(regC.getId()).get();
//    Registration regDFinal = registrationRepository.findById(regD.getId()).get();
//
//    // Verificamos que A siempre está cancelado
//    assertEquals(RegistrationState.CANCELED, regAFinal.getCurrentState());
//
//    // Ahora, verificamos UNO de los DOS posibles estados finales correctos:
//
//    boolean scenario1_PromotionWins = (regCFinal.getCurrentState() == RegistrationState.CONFIRMED);
//    boolean scenario2_CancellationWins = (regCFinal.getCurrentState() == RegistrationState.CANCELED);
//
//    // Verificamos que SOLO UNO de los escenarios ocurrió
//    assertTrue(scenario1_PromotionWins || scenario2_CancellationWins, "UserC no quedó ni Confirmado ni Cancelado");
//    assertFalse(scenario1_PromotionWins && scenario2_CancellationWins, "UserC quedó Confirmado Y Cancelado (Imposible)");
//
//    // Verificamos el estado del evento según el escenario que ganó
//    if (scenario1_PromotionWins) {
//      System.out.println("Resultado: Escenario 1 - Promoción de C ganó.");
//      assertEquals(0, eventoFinal.getAvailableSeats());
//      assertEquals(1, eventoFinal.getParticipants().size()); // Solo C
//      assertEquals(1, eventoFinal.getWaitList().size());    // Solo D
//      assertEquals(regCFinal.getId(), eventoFinal.getParticipants().get(0).getId());
//      assertEquals(regDFinal.getId(), eventoFinal.getWaitList().get(0).getId());
//      assertEquals(RegistrationState.WAITLIST, regDFinal.getCurrentState()); // D sigue esperando
//    } else { // scenario2_CancellationWins debe ser true
//      System.out.println("Resultado: Escenario 2 - Cancelación de C ganó.");
//      assertEquals(0, eventoFinal.getAvailableSeats());
//      assertEquals(1, eventoFinal.getParticipants().size()); // Solo D (fue promovido por A)
//      assertEquals(0, eventoFinal.getWaitList().size());    // Nadie en espera
//      assertEquals(regDFinal.getId(), eventoFinal.getParticipants().get(0).getId());
//      assertEquals(RegistrationState.CONFIRMED, regDFinal.getCurrentState()); // D fue promovido
//    }
//  }
//}