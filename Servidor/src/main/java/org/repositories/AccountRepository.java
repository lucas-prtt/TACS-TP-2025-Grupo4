package org.repositories;

import org.model.accounts.Account;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AccountRepository {
    private final Map<String, Account> accounts = new HashMap<>();

    public Optional<Account> findById(String id) {
        return Optional.ofNullable(accounts.get(id));
    }

    // verifica si existe un username
    public boolean existsByUsername(String username) {
        return accounts.values().stream()
            .anyMatch(account -> account.getUsername().equalsIgnoreCase(username));
    }

    // (Opcional) devolver el Account directamente si existe
    public Optional<Account> findByUsername(String username) {
        return accounts.values().stream()
            .filter(account -> account.getUsername().equalsIgnoreCase(username))
            .findFirst();
    }

    public void save(Account account) {
        accounts.put(account.getId().toString(), account);
    }
}
