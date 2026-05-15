package com.collusion.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "off_campus_housing")
@Getter
@Setter
@NoArgsConstructor
public class OffCampusHousing {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "pnm_id")
    private Pnm pnm;

    @NotBlank
    @Column(name = "street_address", nullable = false)
    private String streetAddress;

    @NotBlank
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @NotBlank
    @Column(name = "state", nullable = false, length = 50)
    private String state;

    @NotBlank
    @Column(name = "zip_code", nullable = false, length = 20)
    private String zipCode;

    public OffCampusHousing(Pnm pnm, String streetAddress, String city,
                            String state, String zipCode) {
        this.pnm           = pnm;
        this.streetAddress = streetAddress;
        this.city          = city;
        this.state         = state;
        this.zipCode       = zipCode;
    }
}
