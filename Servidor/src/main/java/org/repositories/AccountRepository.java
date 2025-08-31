package org.repositories;

import org.dominio.usuarios.Account;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AccountRepository {
    private final Map<String, Account> accounts = new HashMap<>();

    public Optional<Account> findById(String id) {
        return Optional.ofNullable(accounts.get(id));
    }

    public void save(Account account) {
        accounts.put(account.getUuid().toString(), account);
    }
}
