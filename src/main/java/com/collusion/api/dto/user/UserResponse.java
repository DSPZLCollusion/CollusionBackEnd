package com.collusion.api.dto.user;

import java.util.List;

/**
 * Safe user representation returned by all API endpoints.
 * Never includes passwordSalt or passwordHash.
 */
public record UserResponse(
        Long         id,
        String       firstName,
        String       lastName,
        String       email,
        List<String> roles     // e.g. ["ADMIN", "USER"]
) {}