package org.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class HttpErrorResponseBuilder {
    public static ResponseEntity<?> simpleError(String message, HttpStatus status){
        return ResponseEntity
                .status(status)
                .body(Map.of("error", message));
    }
    public static ResponseEntity<?> simpleBadRequest(String message){
        return simpleError(message, HttpStatus.BAD_REQUEST);
    }
    public static ResponseEntity<?> simpleUnauthorized(String message){
        return simpleError(message, HttpStatus.UNAUTHORIZED);
    }
}
