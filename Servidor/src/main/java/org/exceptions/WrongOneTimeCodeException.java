package org.exceptions;


public class WrongOneTimeCodeException extends RuntimeException{

    public WrongOneTimeCodeException() {
        super("Error: se intento usar un OneTimeCode de manera invalida");
    }


}
