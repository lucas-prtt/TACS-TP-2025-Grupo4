package org.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class HttpErrorResponseBuilder {
    public static ResponseEntity<?> simpleError(String message, String errorCode, HttpStatus status){
        return ResponseEntity
                .status(status)
                .body(Map.of("error", message, "error-code", errorCode));
    }
    public static ResponseEntity<?> simpleBadRequest(String message, String errorCode){
        return simpleError(message, errorCode, HttpStatus.BAD_REQUEST);
    }
    public static ResponseEntity<?> simpleUnauthorized(String message, String errorCode){
        return simpleError(message, errorCode, HttpStatus.UNAUTHORIZED);
    }
}
