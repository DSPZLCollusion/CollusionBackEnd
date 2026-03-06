CREATE OR REPLACE FUNCTION assign_role_to_user(
    p_user_id     BIGINT,
    p_role_id     BIGINT,
    p_assigned_by BIGINT DEFAULT NULL
)
    RETURNS SETOF user_roles
    LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
        INSERT INTO user_roles (user_id, role_id, assigned_by)
            VALUES (p_user_id, p_role_id, p_assigned_by)
            ON CONFLICT (user_id, role_id) DO NOTHING
            RETURNING *;
END;
$$;

CREATE OR REPLACE FUNCTION revoke_role_from_user(
    p_user_id BIGINT,
    p_role_id BIGINT
)
    RETURNS SETOF user_roles
    LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
        DELETE FROM user_roles
            WHERE user_id = p_user_id
                AND role_id = p_role_id
            RETURNING *;
END;
$$;

CREATE OR REPLACE FUNCTION revoke_all_roles_from_user(
    p_user_id BIGINT
)
    RETURNS SETOF user_roles
    LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
        DELETE FROM user_roles
            WHERE user_id = p_user_id
            RETURNING *;
END;
$$;

CREATE OR REPLACE FUNCTION get_roles_by_user_id(
    p_user_id BIGINT
)
RETURNS TABLE (
    role_id     BIGINT,
    name        VARCHAR,
    assigned_at TIMESTAMPTZ,
    assigned_by BIGINT
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT
        r.id,
        r.name,
        ur.assigned_at,
        ur.assigned_by
    FROM user_roles ur
    JOIN roles r ON r.id = ur.role_id
    WHERE ur.user_id = p_user_id
    ORDER BY r.name;
END;
$$;

CREATE OR REPLACE FUNCTION get_users_by_role_id(
    p_role_id BIGINT
)
    RETURNS TABLE (
                      user_id     BIGINT,
                      email       VARCHAR,
                      first_name  VARCHAR,
                      last_name   VARCHAR,
                      assigned_at TIMESTAMPTZ
                  )
    LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
        SELECT
            u.id,
            u.email,
            u.first_name,
            u.last_name,
            ur.assigned_at
        FROM user_roles ur
                 JOIN users u ON u.id = ur.user_id
        WHERE ur.role_id = p_role_id
        ORDER BY u.first_name;
END;
$$;

CREATE OR REPLACE FUNCTION user_has_role(
    p_user_id   BIGINT,
    p_role_name VARCHAR
)
    RETURNS BOOLEAN
    LANGUAGE plpgsql
AS $$
DECLARE
    v_exists BOOLEAN;
BEGIN
    SELECT EXISTS (
        SELECT 1
        FROM user_roles ur
                 JOIN roles r ON r.id = ur.role_id
        WHERE ur.user_id = p_user_id
          AND r.name     = p_role_name
    ) INTO v_exists;

    RETURN v_exists;
END;
$$;