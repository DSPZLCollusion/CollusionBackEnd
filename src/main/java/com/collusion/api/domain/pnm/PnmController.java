package com.collusion.api.domain.pnm;

import com.collusion.api.dto.pnm.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pnms")
@RequiredArgsConstructor
public class PnmController {

    private final PnmService pnmService;

    // GET /api/pnms
    // Optional filters: ?status=DELTA&year=FRESHMAN
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<PnmResponse>> getPnms(
            @RequestParam(required = false) PnmStatus status,
            @RequestParam(required = false) ClassYear year) {

        if (status != null && year != null) {
            return ResponseEntity.ok(pnmService.getPnmsByStatusAndYear(status, year));
        }
        if (status != null) {
            return ResponseEntity.ok(pnmService.getPnmsByStatus(status));
        }
        if (year != null) {
            return ResponseEntity.ok(pnmService.getPnmsByYear(year));
        }
        return ResponseEntity.ok(pnmService.getAllPnms());
    }

    // GET /api/pnms/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'DIRECTOR', 'ADMIN')")
    public ResponseEntity<PnmResponse> getPnm(@PathVariable Long id) {
        return ResponseEntity.ok(pnmService.getPnmById(id));
    }

    // POST /api/pnms
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'DIRECTOR', 'ADMIN')")
    public ResponseEntity<Void> createPnm(@Valid @RequestBody PnmRequest request) {
        pnmService.createPnm(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // PUT /api/pnms/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'DIRECTOR', 'ADMIN')")
    public ResponseEntity<Void> updatePnm(@PathVariable Long id,
                                          @Valid @RequestBody PnmRequest request) {
        pnmService.updatePnm(id, request);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/pnms/{id}/status
    // Separate from PUT so the activity log can record status_change distinctly
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('DIRECTOR', 'ADMIN')")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id,
                                             @Valid @RequestBody StatusUpdateRequest request) {
        pnmService.updateStatus(id, request);
        return ResponseEntity.noContent().build();
    }
}