package org.exceptions;

public class AlreadyCanceledException extends RuntimeException {
  public AlreadyCanceledException(String message) {
    super(message);
  }
}
