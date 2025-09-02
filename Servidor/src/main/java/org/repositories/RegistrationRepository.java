package org.repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.dominio.events.Registration;
import org.springframework.stereotype.Repository;

@Repository
public class RegistrationRepository {

  private final Map<String, Registration> registrations = new HashMap<>();

  // Guardar una inscripción nueva
  public void save(Registration reg) throws NullPointerException {
    if (reg == null) throw new NullPointerException("newEvent value is null");
    registrations.put(reg.getId().toString(), reg);
  }

  // Buscar por ID de inscripción
  public Optional<Registration> findById(UUID uuid) {
    return Optional.ofNullable(registrations.get(uuid.toString()));
  }

  // Buscar todas
  public List<Registration> findAll() {
    return new ArrayList<>(registrations.values());
  }

  // Buscar por accountId
  public List<Registration> findByAccountId(UUID accountId) {
    return registrations.values().stream()
        .filter(r -> Objects.equals(r.getUser().getId(), accountId))
        .collect(Collectors.toList());
  }

  // Buscar por eventId
  public List<Registration> findByEventId(UUID eventId) {
    return registrations.values().stream()
        .filter(r -> Objects.equals(r.getEvent().getId(), eventId))
        .collect(Collectors.toList());
  }

  // Eliminar por id
  public boolean deleteById(UUID id) {
    return registrations.remove(id.toString()) != null;
  }

  // Verificar si ya existe inscripción
  public boolean existsByUserAndEvent(UUID accountId, UUID eventId) {
    return registrations.values().stream()
        .anyMatch(r -> Objects.equals(r.getUser().getId(), accountId)
            && Objects.equals(r.getEvent().getId(), eventId));
  }
}

