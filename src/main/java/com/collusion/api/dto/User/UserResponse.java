package com.collusion.api.dto.User;

import com.collusion.api.domain.user.User;

public record UserResponse(
        Integer userId,
        String firstName,
        String lastName,
        String email,
        String roleName
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().getRoleName()
        );
    }
}
