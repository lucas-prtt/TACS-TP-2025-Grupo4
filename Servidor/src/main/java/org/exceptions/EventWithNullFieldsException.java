package org.exceptions;

import lombok.Getter;
import java.util.List;

@Getter
public class EventWithNullFieldsException extends RuntimeException {
    List<String> problems;
    public EventWithNullFieldsException(List<String> problems) {
        super("Algunos campos obligatorios del evento son null");
        this.problems = problems;
    }
}
