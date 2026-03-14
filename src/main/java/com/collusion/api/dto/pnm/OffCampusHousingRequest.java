package com.collusion.api.dto.pnm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record OffCampusHousingRequest(
        @NotBlank(message = "Street address is required")
        String streetAddress,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "State is required")
        String state,

        @NotBlank(message = "Zip code is required")
        @Pattern(regexp = "\\d{5}(-\\d{4})?", message = "Invalid zip code format")
        String zipCode
) {}