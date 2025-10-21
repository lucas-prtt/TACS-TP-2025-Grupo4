package org.exceptions;

import org.springframework.http.ResponseEntity;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(String message) {
        super(message);
    }
}
