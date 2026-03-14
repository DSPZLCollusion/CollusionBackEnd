package com.collusion.api.domain.pnm;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "on_campus_housing")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnCampusHousing {

    // Shares the same id as the pnm — one-to-one shared primary key
    @Id
    private Long pnmId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "pnm_id")
    private Pnm pnm;

    @Column(name = "dorm", nullable = false, columnDefinition = "dorm")
    private Dorm dorm;

    @Column(name = "room_number", nullable = false, length = 20)
    private String roomNumber;
}