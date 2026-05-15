package com.collusion.api.payload.request;

import com.collusion.api.model.enums.ClassYear;
import com.collusion.api.model.enums.Dorm;
import com.collusion.api.model.enums.HousingType;
import com.collusion.api.model.enums.PnmStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PnmRequest {

    @NotBlank
    @Size(max = 50)
    private String firstName;

    @NotBlank
    @Size(max = 50)
    private String lastName;

    @NotNull
    private ClassYear year;        // e.g. "FRESHMAN" — Jackson maps via enum name

    private PnmStatus status;      // nullable — may not be assigned at creation

    @NotNull
    private HousingType housingType;

    // ── On-campus fields (required if housingType = ON_CAMPUS) ───────────────

    private Dorm   dorm;
    private String roomNumber;

    // ── Off-campus fields (required if housingType = OFF_CAMPUS) ─────────────

    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;
}