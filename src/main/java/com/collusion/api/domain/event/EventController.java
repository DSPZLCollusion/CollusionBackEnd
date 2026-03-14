package com.collusion.api.domain.event;

import com.collusion.api.dto.event.EventRequest;
import com.collusion.api.dto.event.EventResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // GET /api/events?upcoming=true
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<EventResponse>> getEvents(
            @RequestParam(required = false, defaultValue = "false") boolean upcoming) {
        return upcoming
                ? ResponseEntity.ok(eventService.getUpcomingEvents())
                : ResponseEntity.ok(eventService.getAllEvents());
    }

    // GET /api/events/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'DIRECTOR', 'ADMIN')")
    public ResponseEntity<EventResponse> getEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    // POST /api/events
    @PostMapping
    @PreAuthorize("hasAnyRole('DIRECTOR', 'ADMIN')")
    public ResponseEntity<Void> createEvent(@Valid @RequestBody EventRequest request) {
        eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // PUT /api/events/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DIRECTOR', 'ADMIN')")
    public ResponseEntity<Void> updateEvent(@PathVariable Long id,
                                            @Valid @RequestBody EventRequest request) {
        eventService.updateEvent(id, request);
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/events/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}