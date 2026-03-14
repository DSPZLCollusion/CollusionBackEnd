package com.collusion.api.domain.pnm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PnmRepository extends JpaRepository<Pnm, Long> {

    List<Pnm> findByStatus(PnmStatus status);

    List<Pnm> findByYear(ClassYear year);

    List<Pnm> findByStatusAndYear(PnmStatus status, ClassYear year);

    // Fetch with housing in one query to avoid N+1
    @Query("SELECT DISTINCT p FROM Pnm p " +
            "LEFT JOIN FETCH p.onCampusHousing " +
            "LEFT JOIN FETCH p.offCampusHousing")
    List<Pnm> findAllWithHousing();

    @Query("SELECT p FROM Pnm p " +
            "LEFT JOIN FETCH p.onCampusHousing " +
            "LEFT JOIN FETCH p.offCampusHousing " +
            "WHERE p.id = :id")
    Optional<Pnm> findByIdWithHousing(Long id);
}