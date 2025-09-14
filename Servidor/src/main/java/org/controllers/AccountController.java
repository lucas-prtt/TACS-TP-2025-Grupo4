package org.controllers;


import static org.utils.SecurityUtils.checkAccountId;

import org.DTOs.events.EventDTO;
import org.DTOs.registrations.RegistrationDTO;
import org.services.EventService;
import org.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<List<RegistrationDTO>> getRegistrations(
                                                            @PathVariable("accountId") UUID accountId,
                                                            @RequestParam(name = "page", required = false) Integer page,
                                                            @RequestParam(name = "limit", required = false) Integer limit){
        if(!checkAccountId(accountId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<RegistrationDTO> registrations = accountService.getRegistrations(accountId, page, limit);
        return ResponseEntity.ok(registrations);
    }


    @GetMapping("/{accountId}/organized-events")
    public ResponseEntity<List<EventDTO>> getOrganizedEvents(@PathVariable(name = "accountId") UUID accountId,
                                                             @RequestParam(name = "page", required = false) Integer page,
                                                             @RequestParam(name = "limit", required = false) Integer limit) {

        if(!checkAccountId(accountId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<EventDTO> events = eventService.getEventsByOrganizer(accountId, page, limit);
        return ResponseEntity.ok(events);
    }

//    @PostMapping
//    public ResponseEntity<?> createAccount(@RequestBody AccountCreateDTO accountCreateDTO) {
//        if (accountService.existsByUsername(accountCreateDTO.getUsername())) {
//            return ResponseEntity.badRequest().body("Username already exists");
//        }
//        Account account = accountService.createAccount(accountCreateDTO);
//        return ResponseEntity.ok(toAccountResponseDTO(account));
//    }
}