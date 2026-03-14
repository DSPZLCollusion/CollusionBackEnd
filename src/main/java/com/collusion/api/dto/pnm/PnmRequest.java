package com.collusion.api.dto.pnm;

import com.collusion.api.domain.pnm.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

// ================================================================
// PnmRequest.java
// ================================================================
public record PnmRequest(

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotNull(message = "Year is required")
        ClassYear year,

        // Nullable — status may not be assigned yet on create
        PnmStatus status,

        @NotNull(message = "Housing type is required")
        HousingType housingType,

        @Valid
        OnCampusHousingRequest onCampusHousing,

        @Valid
        OffCampusHousingRequest offCampusHousing
) {}