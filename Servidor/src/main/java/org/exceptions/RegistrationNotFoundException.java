package org.exceptions;

public class RegistrationNotFoundException extends RuntimeException {
    public RegistrationNotFoundException() {
        super("La inscripción buscada no existe o no pertenece al usuario");
    }
}
