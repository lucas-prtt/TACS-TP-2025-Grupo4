package org.repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.model.events.Event;
import org.model.events.Registration;
import org.model.enums.RegistrationState;
import org.model.accounts.Account;
import org.springframework.boot.CommandLineRunner;

//@Component
public class DataInitializer implements CommandLineRunner {

  private final AccountRepository  userRepository;
  private final RegistrationRepository registrationRepository;
  private final EventRepository eventRepository;

  public DataInitializer(AccountRepository userRepository,
                         RegistrationRepository registrationRepository,
                         EventRepository eventRepository) {
    this.userRepository = userRepository;
    this.registrationRepository = registrationRepository;
    this.eventRepository = eventRepository;
  }

  @Override
  public void run(String... args) throws Exception {
    // Crear usuario
    Account account = new Account();
    account.setUsername("TestingUser");
    userRepository.save(account);

    Account account2 = new Account();
    account2.setUsername("TestingUser2");
    userRepository.save(account2);

    Account account3 = new Account();
    account3.setUsername("TestingUser3");
    userRepository.save(account3);

    // Crear evento inicial
    Event event = Event.Builder()
        .setTitle("Concierto de Jazz")
        .setDescription("Noche de jazz en vivo")
        .setLocation("Teatro Colón")
        .setPrice(new BigDecimal("1500"))
        .setMaxParticipants(2)
        .setStartDateTime(LocalDateTime.now().plusDays(5))
        .setDurationMinutes(120)
        .build();
    eventRepository.save(event);

    // Crear inscripciones
    Registration reg1 = new Registration(event, account, RegistrationState.CONFIRMED);
    registrationRepository.save(reg1);

    Registration reg2 = new Registration(event, account2, RegistrationState.CONFIRMED);
    registrationRepository.save(reg2);

    Registration reg3 = new Registration(event, account3, RegistrationState.WAITLIST);
    registrationRepository.save(reg3);

    event.getParticipants().add(reg1);
    event.getParticipants().add(reg2);
    event.getWaitList().add(reg3);

    // Print de control
    System.out.println(">>> Datos iniciales cargados:");
    System.out.println("Evento: " + event.getTitle() + " (ID: " + event.getId() + ")");

    // Listar todas las inscripciones
    registrationRepository.findAll().forEach(r -> {
      System.out.println("Inscripción ID: " + r.getId()
          + " | Usuario: " + r.getUser().getUsername()
          + " (ID: " + r.getUser().getId() + ")"
          + " | Estado: " + r.getCurrentState());
    });
  }

}
