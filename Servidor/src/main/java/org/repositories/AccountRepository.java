package org.repositories;

import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.model.accounts.Account;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends MongoRepository<Account, UUID> {
    // (Opcional) devolver el Account directamente si existe
    public Optional<Account> findByUsername(String username);

    // verifica si existe un username
    public boolean existsByUsername(String username);
}
