package com.collusion.api.controller;

import com.collusion.api.payload.request.PnmRequest;
import com.collusion.api.payload.response.PnmResponse;
import com.collusion.api.service.PnmService;
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

    @GetMapping
    @PreAuthorize("hasAuthority('USER') or hasAuthority('DIRECTOR') or hasAuthority('ADMIN')")
    public ResponseEntity<List<PnmResponse>> getAll() {
        return ResponseEntity.ok(pnmService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('DIRECTOR') or hasAuthority('ADMIN')")
    public ResponseEntity<PnmResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(pnmService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('DIRECTOR') or hasAuthority('ADMIN')")
    public ResponseEntity<PnmResponse> create(@Valid @RequestBody PnmRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pnmService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('DIRECTOR') or hasAuthority('ADMIN')")
    public ResponseEntity<PnmResponse> update(@PathVariable Long id,
                                              @Valid @RequestBody PnmRequest request) {
        return ResponseEntity.ok(pnmService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pnmService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
