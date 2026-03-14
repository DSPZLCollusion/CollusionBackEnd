package com.collusion.api.dto.event;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record EventRequest(
        @NotBlank(message = "Event name is required")
        String name,

        @NotNull(message = "Event date is required")
        @FutureOrPresent(message = "Event date must be today or in the future")
        LocalDate date
) {}