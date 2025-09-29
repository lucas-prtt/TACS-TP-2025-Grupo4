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

    public Account register(String username, String password, boolean isAdmin) {
        if (accountRepository.existsByUsername(username)) {
            throw new RuntimeException("El usuario ya existe");
        }

        validatePassword(password);

        String hashedPassword = passwordEncoder.encode(password);
        Account account = new Account(username, hashedPassword);

        if (isAdmin) {
            account.getRoles().add(new Role("ROLE_ADMIN"));
        }

        accountRepository.save(account);

        return account;
    }

    public Account login(String username, String password) {
        return accountRepository.findByUsername(username)
            .filter(acc -> passwordEncoder.matches(password, acc.getPassword()))
            .orElseThrow(() -> new RuntimeException("Usuario o contraseña incorrectos"));
    }


    public List<RegistrationDTO> getRegistrations(UUID accountID, Integer page, Integer limit, RegistrationState registrationState) {
        Account account = accountRepository.findById(UUID.fromString(String.valueOf(accountID)))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<RegistrationDTO> processedRegistrations = account.getRegistrations().stream()
            .filter(r -> registrationState == null || r.getCurrentState() == registrationState)
            .map(RegistrationDTO::toRegistrationDTO)
            .collect(Collectors.toList());
        return PageSplitter.getPageList(processedRegistrations, page, limit);
    }
    public List<RegistrationDTO> getRegistrations(UUID accountID) {
        return getRegistrations(accountID, null, null, null);
    }
    public Account getAccountById(UUID accountID){
        return accountRepository.findById(UUID.fromString(String.valueOf(accountID)))
                .orElseThrow(() -> new AccountNotFoundException("Usuario no encontrado"));
    }
    public Account getAccountByUsername(String accountUsername){
        return accountRepository.findByUsername(accountUsername)
                .orElseThrow(() -> new AccountNotFoundException("Usuario no encontrado"));
    }
}