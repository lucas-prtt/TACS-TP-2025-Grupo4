package org.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.utils.HttpErrorResponseBuilder;
import org.utils.I18nManager;

public class InvalidLoginException extends RuntimeException{
    public InvalidLoginException() {
        super("Usuario o contraseña incorrectos");
    }


}
