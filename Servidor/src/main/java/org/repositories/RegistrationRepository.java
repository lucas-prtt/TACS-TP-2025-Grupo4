package org.repositories;

import java.util.List;
import java.util.UUID;
import org.model.events.Registration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationRepository extends MongoRepository<Registration, UUID>{
  // Buscar por accountId
  List<Registration> findByUser_Id(UUID accountId);

  // Buscar por eventId
  List<Registration> findByEvent_Id(UUID eventId);

  // Verificar si ya existe inscripción
  boolean existsByUser_IdAndEvent_Id(UUID accountId, UUID eventId);
}

