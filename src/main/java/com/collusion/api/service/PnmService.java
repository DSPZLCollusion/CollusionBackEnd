package com.collusion.api.service;

import com.collusion.api.model.OffCampusHousing;
import com.collusion.api.model.OnCampusHousing;
import com.collusion.api.model.Pnm;
import com.collusion.api.model.enums.HousingType;
import com.collusion.api.payload.request.PnmRequest;
import com.collusion.api.payload.response.PnmResponse;
import com.collusion.api.repository.PnmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Service layer for PNM operations.
 *
 * WHY A SERVICE LAYER?
 * Controllers should only handle HTTP concerns (parsing requests,
 * returning responses, status codes).  Business logic — like cross-field
 * validation, building entity graphs, managing transactions — lives here.
 *
 * @Transactional on the class means every public method runs in a transaction.
 * This is important because:
 *   - Building a Pnm + OnCampusHousing and saving them must be atomic.
 *   - Accessing lazy collections (housing) inside PnmResponse.from() must
 *     happen inside an open session — the @Transactional annotation keeps
 *     the session open until the method returns.
 *
 * readOnly = true on queries tells Hibernate to skip dirty checking on flush,
 * which is a small but real performance win for read-heavy endpoints.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PnmService {

    private final PnmRepository pnmRepository;

    // ── Read ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PnmResponse getById(Long id) {
        Pnm pnm = findOrThrow(id);
        return PnmResponse.from(pnm);
    }

    @Transactional(readOnly = true)
    public List<PnmResponse> getAll() {
        return pnmRepository.findAll()
                .stream()
                .map(PnmResponse::from)
                .toList();
    }

    // ── Create ────────────────────────────────────────────────────────────────

    public PnmResponse create(PnmRequest request) {
        validateHousingFields(request);

        Pnm pnm = new Pnm();
        pnm.setFirstName(request.getFirstName());
        pnm.setLastName(request.getLastName());
        pnm.setYear(request.getYear());
        pnm.setStatus(request.getStatus());
        pnm.setHousingType(request.getHousingType()); // set before housing helpers

        attachHousing(pnm, request);

        return PnmResponse.from(pnmRepository.save(pnm));
    }

    // ── Update ────────────────────────────────────────────────────────────────

    public PnmResponse update(Long id, PnmRequest request) {
        validateHousingFields(request);

        Pnm pnm = findOrThrow(id);
        pnm.setFirstName(request.getFirstName());
        pnm.setLastName(request.getLastName());
        pnm.setYear(request.getYear());
        pnm.setStatus(request.getStatus());

        // Re-assign housing (helper clears the old record, orphanRemoval deletes it)
        attachHousing(pnm, request);

        return PnmResponse.from(pnmRepository.save(pnm));
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    public void delete(Long id) {
        Pnm pnm = findOrThrow(id);
        pnmRepository.delete(pnm);
        // CascadeType.ALL + orphanRemoval propagates delete to housing sub-table
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Pnm findOrThrow(Long id) {
        return pnmRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "PNM not found with id: " + id));
    }

    /**
     * Cross-field validation — ensures the correct housing sub-fields
     * are present for the chosen housing type.
     * Throwing ResponseStatusException here lets Spring map it directly
     * to a 400 without a separate @ExceptionHandler.
     */
    private void validateHousingFields(PnmRequest req) {
        if (req.getHousingType() == HousingType.ON_CAMPUS) {
            if (req.getDorm() == null || req.getRoomNumber() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "dorm and roomNumber are required for on-campus housing");
            }
        } else {
            if (req.getStreetAddress() == null || req.getCity() == null
                    || req.getState() == null || req.getZipCode() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "streetAddress, city, state, and zipCode are required for off-campus housing");
            }
        }
    }

    /**
     * Builds and attaches the correct housing sub-entity using the
     * convenience helpers on Pnm (which manage the bi-directional link
     * and clear the opposing housing type).
     */
    private void attachHousing(Pnm pnm, PnmRequest req) {
        if (req.getHousingType() == HousingType.ON_CAMPUS) {
            var housing = new OnCampusHousing(pnm, req.getDorm(), req.getRoomNumber());
            pnm.assignOnCampusHousing(housing);
        } else {
            var housing = new OffCampusHousing(
                    pnm, req.getStreetAddress(), req.getCity(),
                    req.getState(), req.getZipCode());
            pnm.assignOffCampusHousing(housing);
        }
    }
}