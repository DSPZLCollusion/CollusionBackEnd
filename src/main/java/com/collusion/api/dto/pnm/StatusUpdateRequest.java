package com.collusion.api.dto.pnm;

import com.collusion.api.domain.pnm.PnmStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(
        @NotNull(message = "Status is required")
        PnmStatus status
) {}