package org.exceptions;


public class InvalidLoginException extends RuntimeException{
    public InvalidLoginException() {
        super("Usuario o contraseña incorrectos");
    }


}
