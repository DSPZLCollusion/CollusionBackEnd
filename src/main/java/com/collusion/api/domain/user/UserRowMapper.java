package com.collusion.api.domain.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public class UserRowMapper {

    /**
     * Maps a row from fn_get_user_by_id / fn_get_all_users.
     * No password fields — safe for general use.
     */
    public static User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .email(rs.getString("email"))
                .build();
    }

    /**
     * Maps a row from fn_get_user_by_email_for_auth.
     * Includes password_salt and password_hash — only used in AuthService.
     */
    public static User mapFullRow(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .email(rs.getString("email"))
                .passwordSalt(rs.getString("password_salt"))
                .passwordHash(rs.getString("password_hash"))
                .build();
    }

    /**
     * Maps a flat row from fn_get_user_with_roles /
     * fn_get_all_users_with_roles / fn_get_user_roles_by_email.
     * One row per user+role — collapsed into User.roles in UserService.
     */
    public static UserRoleRow mapUserRoleRow(ResultSet rs, int rowNum) throws SQLException {
        return new UserRoleRow(
                rs.getLong("user_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getLong("role_id"),
                rs.getString("role_name"),
                rs.getObject("assigned_at", OffsetDateTime.class),
                rs.getLong("assigned_by")
        );
    }
}