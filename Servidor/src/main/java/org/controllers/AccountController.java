package org.controllers;

import static org.DTOs.accounts.AccountResponseDTO.toAccountResponseDTO;

import org.DTOs.accounts.AccountCreateDTO;
import org.DTOs.EventDTO;
import org.DTOs.accounts.AccountResponseDTO;
import org.DTOs.registrations.RegistrationDTO;
import org.exceptions.AccountNotFoundException;
import org.model.enums.RegistrationState;
import org.services.EventService;
import org.model.accounts.Account;
import org.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private EventService eventService;

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponseDTO> getAccountById(@PathVariable(name = "accountId") String accountId) {
        try {
            Account acc = accountService.getAccountById(UUID.fromString(accountId));
            return ResponseEntity.ok(new AccountResponseDTO(acc.getId(), acc.getUsername()));
        }catch (AccountNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/usernames/{accountUsername}")
    public ResponseEntity<AccountResponseDTO> getAccountByUsername(@PathVariable(name = "accountUsername") String accountUsername) {
        try {
            Account acc = accountService.getAccountByUsername(accountUsername);
            return ResponseEntity.ok(new AccountResponseDTO(acc.getId(), acc.getUsername()));
        }catch (AccountNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{accountId}/registrations")
    public ResponseEntity<List<RegistrationDTO>> getRegistrations(@PathVariable(name = "accountId") String accountId,
                                                                  @RequestParam(name = "page", required = false) Integer page,
                                                                  @RequestParam(name = "limit", required = false) Integer limit,
                                                                  @RequestParam(name = "registrationState", required = false) RegistrationState registrationState) {
        List<RegistrationDTO> registrations = accountService.getRegistrations(UUID.fromString(accountId), page, limit, registrationState);
        return ResponseEntity.ok(registrations);
    }

    @GetMapping("/{accountId}/organized-events")
    public ResponseEntity<List<EventDTO>> getOrganizedEvents(@PathVariable(name = "accountId") String accountId,
                                                             @RequestParam(name = "page", required = false) Integer page,
                                                             @RequestParam(name = "limit", required = false) Integer limit) {
        List<EventDTO> events = eventService.getEventsByOrganizer(UUID.fromString(accountId), page, limit);
        return ResponseEntity.ok(events);
    }

    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody AccountCreateDTO accountCreateDTO) {
        if (accountService.existsByUsername(accountCreateDTO.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        Account account = accountService.createAccount(accountCreateDTO);
        return ResponseEntity.ok(toAccountResponseDTO(account));
    }
}