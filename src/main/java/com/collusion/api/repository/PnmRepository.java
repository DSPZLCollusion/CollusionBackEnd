package com.collusion.api.repository;

import com.collusion.api.model.Pnm;
import com.collusion.api.model.enums.ClassYear;
import com.collusion.api.model.enums.HousingType;
import com.collusion.api.model.enums.PnmStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Data access layer for Pnm.
 *
 * FETCH STRATEGY NOTE:
 * The housing sub-entities are LAZY by default (as configured on the entity).
 * Simple findById() / findAll() calls will NOT load the housing record —
 * Hibernate will issue a second query only if you call pnm.getOnCampusHousing().
 *
 * For endpoints that always need the housing detail (e.g. GET /pnms/{id}),
 * use the JOIN FETCH queries below to load everything in a single SQL
 * query rather than triggering N+1 secondary queries.
 */
@Repository
public interface PnmRepository extends JpaRepository<Pnm, Long> {

    List<Pnm> findByYear(ClassYear year);

    List<Pnm> findByStatus(PnmStatus status);

    List<Pnm> findByHousingType(HousingType housingType);

    @Query("SELECT p FROM Pnm p LEFT JOIN FETCH p.onCampusHousing WHERE p.id = :id")
    java.util.Optional<Pnm> findByIdWithOnCampusHousing(Long id);

    @Query("SELECT p FROM Pnm p LEFT JOIN FETCH p.offCampusHousing WHERE p.id = :id")
    java.util.Optional<Pnm> findByIdWithOffCampusHousing(Long id);

    @Query("SELECT p FROM Pnm p JOIN FETCH p.onCampusHousing WHERE p.housingType = 'ON_CAMPUS'")
    List<Pnm> findAllOnCampusWithHousing();

    @Query("SELECT p FROM Pnm p JOIN FETCH p.offCampusHousing WHERE p.housingType = 'OFF_CAMPUS'")
    List<Pnm> findAllOffCampusWithHousing();
}
