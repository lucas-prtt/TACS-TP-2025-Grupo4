package org.controllers;

import static org.DTOs.accounts.AccountResponseDTO.toAccountResponseDTO;

import org.DTOs.accounts.AccountCreateDTO;
import org.DTOs.EventDTO;
import org.DTOs.accounts.AccountResponseDTO;
import org.DTOs.registrations.RegistrationDTO;
import org.services.EventService;
import org.model.accounts.Account;
import org.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private EventService eventService;

    @GetMapping("/{accountId}/registrations")
    public ResponseEntity<List<RegistrationDTO>> getRegistrations(@PathVariable(name = "accountId") String accountId) {
        List<RegistrationDTO> registrations = accountService.getRegistrations(UUID.fromString(accountId));
        return ResponseEntity.ok(registrations);
    }

    @GetMapping("/{accountId}/organized-events")
    public ResponseEntity<List<EventDTO>> getOrganizedEvents(@PathVariable(name = "accountId") String accountId) {
        List<EventDTO> events = eventService.getEventsByOrganizer(UUID.fromString(accountId));
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