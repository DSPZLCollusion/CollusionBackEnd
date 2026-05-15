package com.collusion.api.model;

import com.collusion.api.model.enums.ClassYear;
import com.collusion.api.model.enums.HousingType;
import com.collusion.api.model.enums.PnmStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


@Entity
@Table(name = "pnms")
@Getter
@Setter
@NoArgsConstructor
public class Pnm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @NotBlank
    @Size(max = 50)
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "year", nullable = false,
            columnDefinition = "class_year")
    private ClassYear year;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status",
            columnDefinition = "status")
    private PnmStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "housing_type", nullable = false,
            columnDefinition = "housing_type")
    private HousingType housingType;


    @OneToOne(mappedBy = "pnm",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private OnCampusHousing onCampusHousing;


    @OneToOne(mappedBy = "pnm",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private OffCampusHousing offCampusHousing;


    public void assignOnCampusHousing(OnCampusHousing housing) {
        if (this.offCampusHousing != null) {
            this.offCampusHousing.setPnm(null);
            this.offCampusHousing = null;
        }
        this.onCampusHousing = housing;
        this.housingType     = HousingType.ON_CAMPUS;
        if (housing != null) {
            housing.setPnm(this);
        }
    }

    public void assignOffCampusHousing(OffCampusHousing housing) {
        if (this.onCampusHousing != null) {
            this.onCampusHousing.setPnm(null);
            this.onCampusHousing = null;
        }
        this.offCampusHousing = housing;
        this.housingType      = HousingType.OFF_CAMPUS;
        if (housing != null) {
            housing.setPnm(this);
        }
    }
}
