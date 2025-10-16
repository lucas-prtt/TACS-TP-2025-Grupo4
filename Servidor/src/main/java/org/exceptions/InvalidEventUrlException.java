package org.exceptions;

public class InvalidEventUrlException extends RuntimeException {
    public InvalidEventUrlException(String message) {
        super(message);
    }
}
