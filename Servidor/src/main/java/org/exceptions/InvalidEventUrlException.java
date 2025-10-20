package org.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class InvalidEventUrlException extends RuntimeException {
    public InvalidEventUrlException() {
        super("La url de la imagen no es valida");
    }
}
