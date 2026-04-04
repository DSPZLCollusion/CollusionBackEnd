package com.collusion.api.domain.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
class EventRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EventRepository eventRepository;

    @Test
    public void testFindByDateGreaterThanEqualOrderByDateAsc() {
        // Given
        Event event1 = Event.builder().name("Event 1").date(java.time.LocalDate.of(2024, 6, 30)).build();
        Event event2 = Event.builder().name("Event 2").date(java.time.LocalDate.of(2024, 7, 1)).build();
        Event event3 = Event.builder().name("Event 3").date(java.time.LocalDate.of(2024, 7, 2)).build();

        entityManager.persist(event1);
        entityManager.persist(event2);
        entityManager.persist(event3);
        entityManager.flush();

        // When
        java.util.List<Event> events = eventRepository.findByDateGreaterThanEqualOrderByDateAsc(java.time.LocalDate.of(2024, 7, 1));

        // Then
        assertEquals(2, events.size());
        assertEquals("Event 2", events.get(0).getName());
        assertEquals("Event 3", events.get(1).getName());
    }

}