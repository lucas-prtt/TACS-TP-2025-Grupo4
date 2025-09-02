package RepositoryTests;

import org.dominio.usuarios.Account;
import org.junit.Before;
import org.junit.Test;
import org.repositories.AccountRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;

public class AccountRepositoryTest {
    private AccountRepository accountRepository;
    private Account account;
    private UUID uuid;

    @Before
    public void setUp() {
        accountRepository = new AccountRepository();
        account = new Account();
        account.setUsername("testuser");
    }

    @Test
    public void testSave() {
        accountRepository.save(account);
        // No assertion needed, just ensure no exception is thrown and the account is stored
        Optional<Account> found = accountRepository.findById(account.getId().toString());
        assertTrue(found.isPresent());
    }

    @Test
    public void testFindById_Found() {
        accountRepository.save(account);
        Optional<Account> found = accountRepository.findById(account.getId().toString());
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
        assertEquals(account.getId(), found.get().getId());
    }

    @Test
    public void testFindById_NotFound() {
        Optional<Account> found = accountRepository.findById(UUID.randomUUID().toString());
        assertFalse(found.isPresent());
    }
}
