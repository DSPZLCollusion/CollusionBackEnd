package com.collusion.api.dto.pnm;

import com.collusion.api.domain.pnm.Dorm;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OnCampusHousingRequest(
        @NotNull(message = "Dorm is required for on-campus housing")
        Dorm dorm,

        @NotBlank(message = "Room number is required for on-campus housing")
        String roomNumber
) {}