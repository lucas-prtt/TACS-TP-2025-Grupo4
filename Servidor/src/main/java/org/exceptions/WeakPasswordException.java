package org.exceptions;

public class WeakPasswordException extends RuntimeException {
  public WeakPasswordException(String message) {
    super(message);
  }
}
