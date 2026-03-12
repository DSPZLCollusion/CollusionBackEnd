package com.collusion.api.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final JdbcClient jdbc;
    private final DataSource dataSource;

    // ----------------------------------------------------------------
    // WRITES — procedures called via CALL or SimpleJdbcCall
    // ----------------------------------------------------------------

    /**
     * Creates a new user and returns the generated id.
     * Uses SimpleJdbcCall to capture the OUT parameter p_user_id.
     */
    public Long createUser(String firstName, String lastName,
                           String email, String salt, String hash) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withProcedureName("sp_create_user");

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_first_name", firstName)
                .addValue("p_last_name",  lastName)
                .addValue("p_email",      email)
                .addValue("p_salt",       salt)
                .addValue("p_hash",       hash)
                .addValue("p_user_id",    null, Types.BIGINT);

        var result = call.execute(params);
        return ((Number) result.get("p_user_id")).longValue();
    }

    /**
     * Updates a user's profile info — does not touch password.
     */
    public void updateUser(Long userId, String firstName,
                           String lastName, String email) {
        jdbc.sql("CALL sp_update_user(:userId, :firstName, :lastName, :email)")
                .param("userId",    userId)
                .param("firstName", firstName)
                .param("lastName",  lastName)
                .param("email",     email)
                .update();
    }

    /**
     * Updates a user's password salt and hash together.
     */
    public void updateUserPassword(Long userId, String salt, String hash) {
        jdbc.sql("CALL sp_update_user_password(:userId, :salt, :hash)")
                .param("userId", userId)
                .param("salt",   salt)
                .param("hash",   hash)
                .update();
    }

    // ----------------------------------------------------------------
    // READS — functions called via SELECT * FROM fn_...()
    // ----------------------------------------------------------------

    /**
     * Finds a user by id — no password fields.
     */
    public Optional<User> findById(Long userId) {
        return jdbc.sql("SELECT * FROM fn_get_user_by_id(:id)")
                .param("id", userId)
                .query(UserRowMapper::mapRow)
                .optional();
    }

    /**
     * Finds a user by email — no password fields.
     * Use findByEmailForAuth() when password verification is needed.
     */
    public Optional<User> findByEmail(String email) {
        return jdbc.sql("SELECT * FROM fn_get_user_by_id(:email)")
                .param("email", email)
                .query(UserRowMapper::mapRow)
                .optional();
    }

    /**
     * Finds a user by email including password_salt and password_hash.
     * Only call this from AuthService during login — never expose these fields.
     */
    public Optional<User> findByEmailForAuth(String email) {
        return jdbc.sql("SELECT * FROM fn_get_user_by_email_for_auth(:email)")
                .param("email", email)
                .query(UserRowMapper::mapFullRow)
                .optional();
    }

    /**
     * Checks if an email is already registered.
     */
    public boolean existsByEmail(String email) {
        return findByEmailForAuth(email).isPresent();
    }

    /**
     * Returns all users — no password fields.
     */
    public List<User> findAll() {
        return jdbc.sql("SELECT * FROM fn_get_all_users()")
                .query(UserRowMapper::mapRow)
                .list();
    }

    /**
     * Returns all users with their roles as flat rows.
     * UserService.getAllUsersWithRoles() collapses these into
     * User objects with a List<String> roles field.
     */
    public List<UserRoleRow> findAllWithRoles() {
        return jdbc.sql("SELECT * FROM fn_get_all_users_with_roles()")
                .query(UserRowMapper::mapUserRoleRow)
                .list();
    }

    /**
     * Returns a single user with all their roles as flat rows.
     * Returns multiple rows if the user has multiple roles.
     */
    public List<UserRoleRow> findByIdWithRoles(Long userId) {
        return jdbc.sql("SELECT * FROM fn_get_user_with_roles(:id)")
                .param("id", userId)
                .query(UserRowMapper::mapUserRoleRow)
                .list();
    }

    /**
     * Returns a user's roles by email — used in AuthService
     * to build the JWT claims after password verification.
     */
    public List<UserRoleRow> findRolesByEmail(String email) {
        return jdbc.sql("SELECT * FROM fn_get_user_roles_by_email(:email)")
                .param("email", email)
                .query(UserRowMapper::mapUserRoleRow)
                .list();
    }
}