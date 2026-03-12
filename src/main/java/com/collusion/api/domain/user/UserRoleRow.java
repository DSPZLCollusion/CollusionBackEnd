package com.collusion.api.domain.user;

import java.time.OffsetDateTime;

public record UserRoleRow(
        Long           userId,
        String         firstName,
        String         lastName,
        String         email,
        Long           roleId,
        String         roleName,
        OffsetDateTime assignedAt,
        Long           assignedBy
) {}