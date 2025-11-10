package org.exceptions;

public class InvalidEventUrlException extends RuntimeException {
    public InvalidEventUrlException() {
        super("La url de la imagen no es valida");
    }
}
