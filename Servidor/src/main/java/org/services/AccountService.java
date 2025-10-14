package org.services;

import static org.utils.PasswordValidator.validatePassword;
import org.DTOs.registrations.RegistrationDTO;
import org.exceptions.AccountNotFoundException;
import org.model.accounts.Account;
import org.model.accounts.Role;
import org.model.enums.RegistrationState;
import org.repositories.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.utils.PageSplitter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * @param username Nombre de usuario
     * @param password Contraseña
     * @param isAdmin Si el usuario es administrador
     * @return El usuario registrado
     * @throws RuntimeException si el usuario ya existe o la contraseña es inválida
     */
    public Account register(String username, String password, boolean isAdmin) {
        if (accountRepository.existsByUsername(username)) {
            throw new RuntimeException("El usuario ya existe");
        }

        validatePassword(password);

        String hashedPassword = passwordEncoder.encode(password);
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(hashedPassword);

        if (isAdmin) {
            account.getRoles().add(new Role("ROLE_ADMIN"));
        }

        accountRepository.save(account);

        return account;
    }

    /**
     * Realiza el login de un usuario.
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return El usuario autenticado
     * @throws RuntimeException si el usuario o contraseña son incorrectos
     */
    public Account login(String username, String password) {
        return accountRepository.findByUsername(username)
            .filter(acc -> passwordEncoder.matches(password, acc.getPassword()))
            .orElseThrow(() -> new RuntimeException("Usuario o contraseña incorrectos"));
    }

    
    /**
     * Devuelve el usuario por su ID.
     * @param accountID ID del usuario
     * @return El usuario encontrado
     * @throws AccountNotFoundException si no existe el usuario
     */
    public Account getAccountById(UUID accountID){
        return accountRepository.findById(UUID.fromString(String.valueOf(accountID)))
                .orElseThrow(() -> new AccountNotFoundException("Usuario no encontrado"));
    }
    /**
     * Devuelve el usuario por su nombre de usuario.
     * @param accountUsername Nombre de usuario
     * @return El usuario encontrado
     * @throws AccountNotFoundException si no existe el usuario
     */
    public Account getAccountByUsername(String accountUsername){
        return accountRepository.findByUsername(accountUsername)
                .orElseThrow(() -> new AccountNotFoundException("Usuario no encontrado"));
    }
}