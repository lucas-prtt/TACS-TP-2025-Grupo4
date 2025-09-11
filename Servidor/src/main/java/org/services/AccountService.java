package org.services;

import org.DTOs.accounts.AccountCreateDTO;
import org.DTOs.registrations.RegistrationDTO;
import org.exceptions.AccountNotFoundException;
import org.model.accounts.Account;
import org.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.utils.PageSplitter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Boolean existsByUsername(String username){
        return accountRepository.existsByUsername(username);
    }

    public Account createAccount(AccountCreateDTO accountCreateDTO) {
        Account account = new Account();
        account.setUsername(accountCreateDTO.getUsername());

        accountRepository.save(account);
        return account;
    }

    public List<RegistrationDTO> getRegistrations(UUID accountID, Integer page, Integer limit) {
        Account account = accountRepository.findById(String.valueOf(accountID))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<RegistrationDTO> processedRegistrations = account.getRegistrations().stream()
                .map(RegistrationDTO::toRegistrationDTO)
                .collect(Collectors.toList());
        return PageSplitter.getPageList(processedRegistrations, page, limit);
    }
    public List<RegistrationDTO> getRegistrations(UUID accountID) {
        return getRegistrations(accountID, null, null);
    }
    public Account getAccountById(UUID accountID){
        return accountRepository.findById(String.valueOf(accountID))
                .orElseThrow(() -> new AccountNotFoundException("Usuario no encontrado"));
    }
    public Account getAccountByUsername(String accountUsername){
        return accountRepository.findByUsername(accountUsername)
                .orElseThrow(() -> new AccountNotFoundException("Usuario no encontrado"));
    }
}