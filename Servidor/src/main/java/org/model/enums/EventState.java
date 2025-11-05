package org.model.enums;

/**
 * Enum que representa los posibles estados de un evento.
 * EVENT_OPEN: El evento está abierto y acepta inscripciones.
 * EVENT_CLOSED: El evento está cerrado y no acepta inscripciones.
 * EVENT_PAUSED: El evento está pausado temporalmente.
 */
public enum EventState {
    EVENT_OPEN,
    EVENT_CLOSED,
    EVENT_CANCELLED,
}