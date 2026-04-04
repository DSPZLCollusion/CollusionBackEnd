package com.collusion.api.domain.event;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    @Test
    void testEventCreation() {
        Event event = Event.builder()
                .name("Test Event")
                .date(java.time.LocalDate.of(2024, 6, 30))
                .build();

        assertNotNull(event);
        assertEquals("Test Event", event.getName());
        assertEquals(java.time.LocalDate.of(2024, 6, 30), event.getDate());
    }

    @Test
    void testNameSetter() {
        Event event = new Event();
        event.setName("Updated Event Name");
        assertEquals("Updated Event Name", event.getName());
    }

    @Test
    void testDateSetter() {
        Event event = new Event();
        java.time.LocalDate newDate = java.time.LocalDate.of(2024, 7, 1);
        event.setDate(newDate);
        assertEquals(newDate, event.getDate());
    }
}