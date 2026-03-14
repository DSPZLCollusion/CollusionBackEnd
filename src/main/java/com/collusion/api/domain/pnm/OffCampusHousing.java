package com.collusion.api.domain.pnm;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "off_campus_housing")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OffCampusHousing {

    @Id
    private Long pnmId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "pnm_id")
    private Pnm pnm;

    @Column(name = "street_address", nullable = false, length = 255)
    private String streetAddress;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "state", nullable = false, length = 50)
    private String state;

    @Column(name = "zip_code", nullable = false, length = 20)
    private String zipCode;
}