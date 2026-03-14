package com.collusion.api.domain.event;

import com.collusion.api.dto.event.EventRequest;
import com.collusion.api.dto.event.EventResponse;
import com.collusion.api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    // ----------------------------------------------------------------
    // CREATE
    // ----------------------------------------------------------------

    @Transactional
    public Long createEvent(EventRequest request) {
        Event event = Event.builder()
                .name(request.name())
                .date(request.date())
                .build();
        return eventRepository.save(event).getId();
    }

    // ----------------------------------------------------------------
    // UPDATE
    // ----------------------------------------------------------------

    @Transactional
    public void updateEvent(Long eventId, EventRequest request) {
        Event event = findOrThrow(eventId);
        event.setName(request.name());
        event.setDate(request.date());
        eventRepository.save(event);
    }

    // ----------------------------------------------------------------
    // DELETE
    // ----------------------------------------------------------------

    @Transactional
    public void deleteEvent(Long eventId) {
        findOrThrow(eventId);
        eventRepository.deleteById(eventId);
    }

    // ----------------------------------------------------------------
    // READS
    // ----------------------------------------------------------------

    public EventResponse getEventById(Long eventId) {
        return EventResponse.from(findOrThrow(eventId));
    }

    public List<EventResponse> getAllEvents() {
        return eventRepository.findAllByOrderByDateAsc()
                .stream()
                .map(EventResponse::from)
                .toList();
    }

    public List<EventResponse> getUpcomingEvents() {
        return eventRepository
                .findByDateGreaterThanEqualOrderByDateAsc(LocalDate.now())
                .stream()
                .map(EventResponse::from)
                .toList();
    }

    // ----------------------------------------------------------------
    // PRIVATE HELPERS
    // ----------------------------------------------------------------

    private Event findOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", eventId));
    }
}