package org.exceptions;


public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException() {
        super("Usuario no encontrado");
    }
}
