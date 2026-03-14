package com.collusion.api.domain.pnm;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pnms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pnm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    // AttributeConverter autoApply = true means no @Convert needed here
    @Column(name = "year", nullable = false, columnDefinition = "class_year")
    private ClassYear year;

    // Nullable — a PNM may not have been evaluated yet
    @Column(name = "status", columnDefinition = "status")
    private PnmStatus status;

    @Column(name = "housing_type", nullable = false, columnDefinition = "housing_type")
    private HousingType housingType;

    // Bidirectional — only one will be non-null depending on housingType
    @OneToOne(mappedBy = "pnm", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private OnCampusHousing onCampusHousing;

    @OneToOne(mappedBy = "pnm", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private OffCampusHousing offCampusHousing;

    // ----------------------------------------------------------------
    // Convenience helpers for setting housing — ensures only one
    // housing type is set at a time
    // ----------------------------------------------------------------

    public void setOnCampusHousing(OnCampusHousing housing) {
        this.offCampusHousing = null;
        this.onCampusHousing = housing;
        this.housingType = HousingType.ON_CAMPUS;
        if (housing != null) housing.setPnm(this);
    }

    public void setOffCampusHousing(OffCampusHousing housing) {
        this.onCampusHousing = null;
        this.offCampusHousing = housing;
        this.housingType = HousingType.OFF_CAMPUS;
        if (housing != null) housing.setPnm(this);
    }
}