package org.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.utils.HttpErrorResponseBuilder;
import org.utils.I18nManager;

public class AccountAlreadyExistsException extends RuntimeException{
    public AccountAlreadyExistsException() {
        super("El usuario ya existe");
    }
}
