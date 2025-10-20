package org.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AccountNotFoundException extends HttpResponseError {
    public AccountNotFoundException() {
        super("Usuario no encontrado");
    }

    @Override
    public ResponseEntity<?> httpResponse(String lang) {
        return basicResponse(lang, "ERROR_ACCOUNT_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
