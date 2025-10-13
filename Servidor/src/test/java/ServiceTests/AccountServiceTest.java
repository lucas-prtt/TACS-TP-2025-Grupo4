package ServiceTests;

import org.DTOs.registrations.RegistrationDTO;
import org.exceptions.AccountNotFoundException;
import org.junit.jupiter.api.Test;
import org.model.accounts.Account;

import org.model.enums.RegistrationState;
import org.model.events.Event;
import org.model.events.Registration;
import org.repositories.AccountRepository;
import org.services.AccountService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;



import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // -------------------------
    // TEST REGISTER
    // -------------------------
    @Test
    public void testRegister_NewUser_ShouldSaveAccount() {
        String username = "user1";
        String password = "Password1!"; // contraseña válida
        boolean isAdmin = true;

        when(accountRepository.existsByUsername(username)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("hashedPassword");

        Account account = accountService.register(username, password, isAdmin);

        assertNotNull(account);
        assertEquals(username, account.getUsername());
        assertEquals("hashedPassword", account.getPassword());
        assertTrue(account.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN")));

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(account, captor.getValue());
    }

    @Test
    public void testRegister_ExistingUser_ShouldThrow() {
        when(accountRepository.existsByUsername("user1")).thenReturn(true);
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            accountService.register("user1", "password", false));
        assertEquals("El usuario ya existe", exception.getMessage());
    }

    // -------------------------
    // TEST LOGIN
    // -------------------------
    @Test
    public void testLogin_CorrectPassword_ShouldReturnAccount() {
        String username = "user1";
        String password = "hashedPassword";
        Account acc = new Account();
        acc.setPassword(password);
        acc.setUsername(username);

        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(acc));
        when(passwordEncoder.matches(password, "hashedPassword")).thenReturn(true);

        Account result = accountService.login(username, password);
        assertEquals(acc, result);
    }

    @Test
    public void testLogin_WrongPassword_ShouldThrow() {
        String username = "user1";
        String password = "password";
        Account acc = new Account();
        acc.setPassword(password);
        acc.setUsername(username);

        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(acc));
        when(passwordEncoder.matches(password, "hashedPassword")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            accountService.login(username, password));
        assertEquals("Usuario o contraseña incorrectos", exception.getMessage());
    }

    @Test
    public void testLogin_UserNotFound_ShouldThrow() {
        when(accountRepository.findByUsername("user1")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            accountService.login("user1", "password"));
        assertEquals("Usuario o contraseña incorrectos", exception.getMessage());
    }

    // -------------------------
    // TEST GET ACCOUNT BY ID
    // -------------------------
    @Test
    public void testGetAccountById_Found_ShouldReturnAccount() {
        UUID id = UUID.randomUUID();
        Account acc = new Account();
        acc.setUsername("user1");
        acc.setPassword("pass");
        when(accountRepository.findById(id)).thenReturn(Optional.of(acc));

        Account result = accountService.getAccountById(id);
        assertEquals(acc, result);
    }

    @Test
    public void testGetAccountById_NotFound_ShouldThrow() {
        UUID id = UUID.randomUUID();
        when(accountRepository.findById(UUID.fromString(String.valueOf(id)))).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.getAccountById(id));
    }

    // -------------------------
    // TEST GET ACCOUNT BY USERNAME
    // -------------------------
    @Test
    public void testGetAccountByUsername_Found_ShouldReturnAccount() {
        String username = "user1";
        String password = "pass";
        Account acc = new Account();
        acc.setPassword(password);
        acc.setUsername(username);
        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(acc));

        Account result = accountService.getAccountByUsername(username);
        assertEquals(acc, result);
    }

    @Test
    public void testGetAccountByUsername_NotFound_ShouldThrow() {
        when(accountRepository.findByUsername("user1")).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> accountService.getAccountByUsername("user1"));
    }

}

