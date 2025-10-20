package org.exceptions;

import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.utils.HttpErrorResponseBuilder;
import org.utils.I18nManager;

import java.util.List;

@Getter
public class EventWithNullFieldsException extends RuntimeException {
    List<String> problems;
    public EventWithNullFieldsException(List<String> problems) {
        super("Algunos campos obligatorios del evento son null");
        this.problems = problems;
    }
}
