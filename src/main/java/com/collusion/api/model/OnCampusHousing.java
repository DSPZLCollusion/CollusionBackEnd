package com.collusion.api.model;

import com.collusion.api.model.enums.Dorm;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "on_campus_housing")
@Getter
@Setter
@NoArgsConstructor
public class OnCampusHousing {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "pnm_id")
    private Pnm pnm;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "dorm", nullable = false,
            columnDefinition = "dorm")
    private Dorm dorm;

    @NotBlank
    @Column(name = "room_number", nullable = false, length = 20)
    private String roomNumber;

    public OnCampusHousing(Pnm pnm, Dorm dorm, String roomNumber) {
        this.pnm        = pnm;
        this.dorm       = dorm;
        this.roomNumber = roomNumber;
    }
}