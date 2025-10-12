package org.exceptions;

public class DateAlreadySetException extends RuntimeException {
    public DateAlreadySetException(String message) {
        super(message);
    }
}
