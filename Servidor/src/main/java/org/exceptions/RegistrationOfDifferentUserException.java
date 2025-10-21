package org.exceptions;

public class RegistrationOfDifferentUserException extends RuntimeException {
    public RegistrationOfDifferentUserException() {
        super("La incripcion elegida pertenece a otro usuario");
    }
}
