package org.exceptions;

import org.springframework.http.ResponseEntity;
import org.utils.HttpErrorResponseBuilder;
import org.utils.I18nManager;

import java.util.List;

public class WeakPasswordException extends HttpResponseError{
  List<String> problems;
  public WeakPasswordException(List<String> problems) {
    super("Error: Contraseña insegura - " + String.join("; ", problems));
    this.problems = problems;
  }

  public ResponseEntity<?> httpResponse(String lang) {
    return HttpErrorResponseBuilder.simpleBadRequest(I18nManager.get("ERROR_WEAK_PASSWORD", lang, String.join("; ", problems.stream().map(prob -> I18nManager.get(prob, lang)).toList())));
  }
}
