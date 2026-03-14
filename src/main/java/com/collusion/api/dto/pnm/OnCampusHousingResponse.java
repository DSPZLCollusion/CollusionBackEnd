package com.collusion.api.dto.pnm;

import com.collusion.api.domain.pnm.Dorm;

public record OnCampusHousingResponse(
        Dorm   dorm,
        String roomNumber
) {}