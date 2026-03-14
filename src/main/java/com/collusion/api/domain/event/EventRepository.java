package com.collusion.api.domain.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // All events on or after today — used for upcoming events endpoint
    List<Event> findByDateGreaterThanEqualOrderByDateAsc(LocalDate date);

    // All events ordered by date ascending
    List<Event> findAllByOrderByDateAsc();
}