package org.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.utils.HttpErrorResponseBuilder;
import org.utils.I18nManager;

public class AccountAlreadyExistsException extends HttpResponseError{
    public AccountAlreadyExistsException() {
        super("El usuario ya existe");
    }
    public ResponseEntity<?> httpResponse(String lang){
        return basicResponse(lang, "ERROR_USER_ALREADY_EXISTS", HttpStatus.BAD_REQUEST);
    }
}
