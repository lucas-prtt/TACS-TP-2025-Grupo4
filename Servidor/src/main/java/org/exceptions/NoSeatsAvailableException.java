package org.exceptions;

public class NoSeatsAvailableException extends RuntimeException {
  public NoSeatsAvailableException(String message) {
    super(message);
  }
}
