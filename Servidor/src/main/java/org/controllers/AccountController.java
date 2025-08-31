package org.controllers;

import org.DTOs.AccountDTO;
import org.dominio.usuarios.Account;
import org.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody AccountDTO accountDTO) {
        if (accountDTO.getUuid() == null) {
            accountDTO.setUuid(UUID.randomUUID());
        }
        Account account = accountService.createAccount(accountDTO);
        return ResponseEntity.ok(account);
    }
}