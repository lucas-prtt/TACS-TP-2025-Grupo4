package org.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.utils.HttpErrorResponseBuilder;
import org.utils.I18nManager;

public abstract class HttpResponseError extends RuntimeException{
    public HttpResponseError(String errorMessage) {
        super(errorMessage);
    }
    public ResponseEntity<?> basicResponse(String lang, String key, HttpStatus status){
        return HttpErrorResponseBuilder.simpleError(I18nManager.get(key, lang), status);
    }

    public abstract ResponseEntity<?> httpResponse(String lang);
}
