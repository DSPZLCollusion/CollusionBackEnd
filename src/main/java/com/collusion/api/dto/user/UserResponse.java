package com.collusion.api.dto.user;

import com.collusion.api.domain.user.User;

import java.util.List;

public record UserResponse(
        Long         id,
        String       firstName,
        String       lastName,
        String       email,
        List<String> roles
) {
    /**
     * Static factory mapping User entity → UserResponse.
     * Deliberately excludes passwordSalt and passwordHash.
     */
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRoleNames()
        );
    }
}