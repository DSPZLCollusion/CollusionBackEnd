package com.collusion.api.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @Email(message = "Must be a valid email address")
        @NotBlank(message = "Email is required")
        String email,

        @Size(min = 8, message = "Password must be at least 8 characters")
        String password
) {}