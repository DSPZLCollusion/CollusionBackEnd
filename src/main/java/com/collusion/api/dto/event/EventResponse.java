package com.collusion.api.dto.event;

import com.collusion.api.domain.event.Event;

import java.time.LocalDate;

public record EventResponse(
        Long      id,
        String    name,
        LocalDate date
) {
    public static EventResponse from(Event event) {
        return new EventResponse(
                event.getId(),
                event.getName(),
                event.getDate()
        );
    }
}