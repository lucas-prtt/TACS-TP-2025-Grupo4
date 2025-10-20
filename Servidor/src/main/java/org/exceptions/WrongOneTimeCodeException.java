package org.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class WrongOneTimeCodeException extends HttpResponseError{

    public WrongOneTimeCodeException() {
        super("Error: se intento usar un OneTimeCode de manera invalida");
    }

    @Override
    public ResponseEntity<?> httpResponse(String lang) {
        return basicResponse(lang, "ERROR_INVALID_ONE_TIME_CODE", HttpStatus.UNAUTHORIZED);
    }
}
