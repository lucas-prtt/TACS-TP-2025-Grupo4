package org.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class WrongOneTimeCodeException extends RuntimeException{

    public WrongOneTimeCodeException() {
        super("Error: se intento usar un OneTimeCode de manera invalida");
    }


}
