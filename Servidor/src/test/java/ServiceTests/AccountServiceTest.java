package ServiceTests;

import org.DTOs.AccountDTO;
import org.dominio.usuarios.Account;
import org.junit.Before;
import org.junit.Test;
import org.repositories.AccountRepository;
import org.services.AccountService;

import java.util.UUID;

import static org.junit.Assert.*;

public class AccountServiceTest {
    private AccountService accountService;
    private AccountRepository accountRepository;

    @Before
    public void setUp() {
        accountRepository = new AccountRepository();
        accountService = new AccountService();
        // Inyectar el repo usando reflexión porque usa @Autowired
        try {
            java.lang.reflect.Field repoField = AccountService.class.getDeclaredField("accountRepository");
            repoField.setAccessible(true);
            repoField.set(accountService, accountRepository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateAccount() {
        AccountDTO dto = new AccountDTO();
        UUID uuid = UUID.randomUUID();
        dto.setUuid(uuid);
        dto.setUsername("nuevoUsuario");

        Account account = accountService.createAccount(dto);

        assertNotNull(account);
        assertEquals(uuid, account.getUuid());
        assertEquals("nuevoUsuario", account.getUsername());
        assertTrue(accountRepository.findById(uuid.toString()).isPresent());
    }

    @Test
    public void testGetRegistrations_Empty() {
        AccountDTO dto = new AccountDTO();
        UUID uuid = UUID.randomUUID();
        dto.setUuid(uuid);
        dto.setUsername("nuevoUsuario");
        accountService.createAccount(dto);

        assertTrue(accountService.getRegistrations(uuid).isEmpty());
    }
}
