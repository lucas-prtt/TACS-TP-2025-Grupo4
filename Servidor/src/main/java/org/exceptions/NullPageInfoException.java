package org.exceptions;

public class NullPageInfoException extends RuntimeException {
    public NullPageInfoException() {
        super("La informacion de paginacion contiene null");
    }
}
