package org.eventServerClient.dtos.event;

public enum EventStateDTO {
    EVENT_OPEN,
    EVENT_CLOSED,
    EVENT_PAUSED      // Retrocompatible con el estado pausado, aunque ya no se usa más.
}
