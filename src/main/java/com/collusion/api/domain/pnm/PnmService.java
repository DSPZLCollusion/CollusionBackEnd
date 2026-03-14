package com.collusion.api.domain.pnm;

import com.collusion.api.dto.pnm.*;
import com.collusion.api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PnmService {

    private final PnmRepository pnmRepository;

    // ----------------------------------------------------------------
    // CREATE
    // ----------------------------------------------------------------

    @Transactional
    public Long createPnm(PnmRequest request) {
        validateHousingConsistency(request);

        Pnm pnm = Pnm.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .year(request.year())
                .status(request.status())
                .housingType(request.housingType())
                .build();

        attachHousing(pnm, request);

        return pnmRepository.save(pnm).getId();
    }

    // ----------------------------------------------------------------
    // UPDATE
    // ----------------------------------------------------------------

    @Transactional
    public void updatePnm(Long pnmId, PnmRequest request) {
        validateHousingConsistency(request);

        Pnm pnm = findOrThrow(pnmId);

        pnm.setFirstName(request.firstName());
        pnm.setLastName(request.lastName());
        pnm.setYear(request.year());
        pnm.setHousingType(request.housingType());

        attachHousing(pnm, request);

        pnmRepository.save(pnm);
    }

    /**
     * PATCH — updates status only.
     * Kept separate from updatePnm so the activity log can record
     * status_change distinctly from info_change.
     */
    @Transactional
    public void updateStatus(Long pnmId, StatusUpdateRequest request) {
        Pnm pnm = findOrThrow(pnmId);
        pnm.setStatus(request.status());
        pnmRepository.save(pnm);
    }

    // ----------------------------------------------------------------
    // READS
    // ----------------------------------------------------------------

    public PnmResponse getPnmById(Long pnmId) {
        return PnmResponse.from(
                pnmRepository.findByIdWithHousing(pnmId)
                        .orElseThrow(() -> new ResourceNotFoundException("PNM", pnmId))
        );
    }

    public List<PnmResponse> getAllPnms() {
        return pnmRepository.findAllWithHousing()
                .stream()
                .map(PnmResponse::from)
                .toList();
    }

    public List<PnmResponse> getPnmsByStatus(PnmStatus status) {
        return pnmRepository.findByStatus(status)
                .stream()
                .map(PnmResponse::from)
                .toList();
    }

    public List<PnmResponse> getPnmsByYear(ClassYear year) {
        return pnmRepository.findByYear(year)
                .stream()
                .map(PnmResponse::from)
                .toList();
    }

    public List<PnmResponse> getPnmsByStatusAndYear(PnmStatus status, ClassYear year) {
        return pnmRepository.findByStatusAndYear(status, year)
                .stream()
                .map(PnmResponse::from)
                .toList();
    }

    // ----------------------------------------------------------------
    // PRIVATE HELPERS
    // ----------------------------------------------------------------

    /**
     * Validates that the correct housing detail object is present
     * for the given housingType — catches mismatches before hitting the DB.
     */
    private void validateHousingConsistency(PnmRequest request) {
        if (request.housingType() == HousingType.ON_CAMPUS
                && request.onCampusHousing() == null) {
            throw new IllegalArgumentException(
                    "onCampusHousing is required when housingType is ON_CAMPUS");
        }
        if (request.housingType() == HousingType.OFF_CAMPUS
                && request.offCampusHousing() == null) {
            throw new IllegalArgumentException(
                    "offCampusHousing is required when housingType is OFF_CAMPUS");
        }
    }

    /**
     * Attaches the correct housing entity to the Pnm.
     * The Pnm convenience setters clear the opposite type automatically.
     */
    private void attachHousing(Pnm pnm, PnmRequest request) {
        if (request.housingType() == HousingType.ON_CAMPUS) {
            OnCampusHousingRequest h = request.onCampusHousing();
            pnm.setOnCampusHousing(OnCampusHousing.builder()
                    .dorm(h.dorm())
                    .roomNumber(h.roomNumber())
                    .build());
        } else {
            OffCampusHousingRequest h = request.offCampusHousing();
            pnm.setOffCampusHousing(OffCampusHousing.builder()
                    .streetAddress(h.streetAddress())
                    .city(h.city())
                    .state(h.state())
                    .zipCode(h.zipCode())
                    .build());
        }
    }

    private Pnm findOrThrow(Long pnmId) {
        return pnmRepository.findById(pnmId)
                .orElseThrow(() -> new ResourceNotFoundException("PNM", pnmId));
    }
}