package org.exceptions;

public class EventRegistrationsClosedException extends RuntimeException {
    public EventRegistrationsClosedException(String message) {
        super(message);
    }
}
