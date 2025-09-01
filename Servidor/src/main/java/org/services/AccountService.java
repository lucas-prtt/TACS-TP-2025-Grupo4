package org.services;

import org.DTOs.AccountDTO;
import org.dominio.usuarios.Account;
import org.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Account createAccount(AccountDTO accountDTO) {
        Account account = new Account();
        account.setId(accountDTO.getUuid());
        account.setUsername(accountDTO.getUsername());
        account.setSalt(accountDTO.getSalt());
        account.setPasswordHash(accountDTO.getPasswordHash());

        accountRepository.save(account);
        return account;
    }
}