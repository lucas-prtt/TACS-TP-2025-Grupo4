package org.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class InvalidEventUrlException extends HttpResponseError {
    public InvalidEventUrlException() {
        super("La url de la imagen no es valida");
    }

    @Override
    public ResponseEntity<?> httpResponse(String lang) {
        return basicResponse(lang, "ERROR_INVALID_URL", HttpStatus.BAD_REQUEST);
    }
}
