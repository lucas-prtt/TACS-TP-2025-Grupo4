package org.exceptions;


public class AccountAlreadyExistsException extends RuntimeException{
    public AccountAlreadyExistsException() {
        super("El usuario ya existe");
    }
}
