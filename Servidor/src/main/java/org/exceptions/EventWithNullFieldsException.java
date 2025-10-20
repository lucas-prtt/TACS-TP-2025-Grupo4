package org.exceptions;

import org.springframework.http.ResponseEntity;
import org.utils.HttpErrorResponseBuilder;
import org.utils.I18nManager;

import java.util.List;

public class EventWithNullFieldsException extends HttpResponseError {
    List<String> problems;
    public EventWithNullFieldsException(List<String> problems) {
        super("Algunos campos obligatorios del evento son null");
        this.problems = problems;
    }

    @Override
    public ResponseEntity<?> httpResponse(String lang) {
        return HttpErrorResponseBuilder.simpleBadRequest(I18nManager.get("ERROR_EVENT_NULL_FIELDS", lang, String.join(", ", problems.stream().map(p->I18nManager.get(p, lang)).toList())));
    }
}
