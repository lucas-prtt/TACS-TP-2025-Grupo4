package org.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException() {
        super("Usuario no encontrado");
    }
}
