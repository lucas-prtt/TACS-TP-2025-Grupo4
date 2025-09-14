package org.exceptions;

public class AlreadyInWaitlistException extends AlreadyRegisteredException {
    public AlreadyInWaitlistException(String message) {
        super(message);
    }
}
