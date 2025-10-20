package org.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class NullPageInfoException extends RuntimeException {
    public NullPageInfoException() {
        super("La informacion de paginacion contiene null");
    }
}
