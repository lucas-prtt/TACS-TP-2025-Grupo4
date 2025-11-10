package org.exceptions;

import lombok.Getter;


import java.util.List;
@Getter
public class WeakPasswordException extends RuntimeException{
  List<String> problems;
  public WeakPasswordException(List<String> problems) {
    super("Error: Contraseña insegura - " + String.join("; ", problems));
    this.problems = problems;
  }
}
