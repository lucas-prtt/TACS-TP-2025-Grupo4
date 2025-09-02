package org.services;

import org.DTOs.AccountDTO;
import org.DTOs.AccountRegistrationDTO;
import org.DTOs.RegistrationDTO;
import org.dominio.usuarios.Account;
import org.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Account createAccount(AccountDTO accountDTO) {
        Account account = new Account();
        account.setUuid(accountDTO.getUuid());
        account.setUsername(accountDTO.getUsername());
        account.setSalt(accountDTO.getSalt());
        account.setPasswordHash(accountDTO.getPasswordHash());

        accountRepository.save(account);
        return account;
    }

    public List<AccountRegistrationDTO> getRegistrations(UUID accountID) {
        Account account = accountRepository.findById(String.valueOf(accountID))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return account.getRegistrations().stream()
                .map(reg -> new AccountRegistrationDTO(
                        reg.getEvent().getId(),
                        reg.getEvent().getTitle(),
                        reg.getEvent().getDescription(),
                        reg.getState().toString()
                ))
                .collect(Collectors.toList());
    }
}