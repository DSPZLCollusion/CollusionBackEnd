package com.collusion.api.dto.pnm;

public record OffCampusHousingResponse(
        String streetAddress,
        String city,
        String state,
        String zipCode
) {}