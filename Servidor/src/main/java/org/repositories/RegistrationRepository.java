package org.repositories;

import java.util.List;
import java.util.UUID;

import org.model.enums.RegistrationState;
import org.model.events.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

  Page<Registration> findByUser_Id(UUID accountId, Pageable pageable);


  Page<Registration> findByUser_IdAndCurrentState(UUID accountId, RegistrationState registrationState, Pageable of);

  Page<Registration> findByEvent_IdAndCurrentState(UUID eventId, RegistrationState registrationState, Pageable of);

  Page<Registration> findByEvent_Id(UUID eventId, Pageable pageable);

  long countByEvent_Id(UUID eventId);

  long countByEvent_IdAndCurrentStateNot(UUID eventId, RegistrationState registrationState);

}

