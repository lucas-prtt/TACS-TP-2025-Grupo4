package RepositoryTests;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.dominio.events.Event;
import org.dominio.events.Registration;
import org.dominio.events.RegistrationState;
import org.dominio.usuarios.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import org.repositories.RegistrationRepository;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationRepositoryTest {

  private RegistrationRepository repository;
  private Account account;
  private Event event;
  private Registration registration;

  @BeforeEach
  void setUp() {
    repository = new RegistrationRepository();

    account = new Account();
    account.setId(UUID.randomUUID());
    account.setUsername("testuser");

    event = Event.Builder().setTitle("Fiesta de cumpleaños").setDescription("Juan perez cumple años").setLocation("CABA").setPrice(new BigDecimal(0)).setMaxParticipants(40).setStartDateTime(LocalDateTime.now().plusDays(1)).setDurationMinutes(180).build();

    registration = new Registration(event, account, RegistrationState.CONFIRMED);


  }

  @Test
  void saveAndFindById_shouldPersistRegistration() {
    repository.save(registration);

    Optional<Registration> found = repository.findById(registration.getId());

    assertTrue(found.isPresent());
    assertEquals(registration, found.get());
  }

  @Test
  void save_nullRegistration_shouldThrowException() {
    assertThrows(NullPointerException.class, () -> repository.save(null));
  }

  @Test
  void findAll_shouldReturnAllRegistrations() {
    repository.save(registration);

    List<Registration> all = repository.findAll();

    assertEquals(1, all.size());
    assertTrue(all.contains(registration));
  }

  @Test
  void findByUserId_shouldReturnRegistrationsForUser() {
    repository.save(registration);

    List<Registration> userRegistrations = repository.findByUserId(account.getId());

    assertEquals(1, userRegistrations.size());
    assertEquals(registration, userRegistrations.getFirst());
  }

  @Test
  void findByEventId_shouldReturnRegistrationsForEvent() {
    repository.save(registration);

    List<Registration> eventRegistrations = repository.findByEventId(event.getId());

    assertEquals(1, eventRegistrations.size());
    assertEquals(registration, eventRegistrations.getFirst());
  }

  @Test
  void deleteById_shouldRemoveRegistration() {
    repository.save(registration);

    boolean deleted = repository.deleteById(registration.getId());

    assertTrue(deleted);
    assertFalse(repository.findById(registration.getId()).isPresent());
  }

  @Test
  void deleteById_notExisting_shouldReturnFalse() {
    boolean deleted = repository.deleteById(UUID.randomUUID());

    assertFalse(deleted);
  }

  @Test
  void existsByUserAndEvent_shouldReturnTrueWhenExists() {
    repository.save(registration);

    boolean exists = repository.existsByUserAndEvent(account.getId(), event.getId());

    assertTrue(exists);
  }

  @Test
  void existsByUserAndEvent_shouldReturnFalseWhenNotExists() {
    boolean exists = repository.existsByUserAndEvent(account.getId(), event.getId());

    assertFalse(exists);
  }
}

