-- ============================================================
-- STORED PROCEDURES & FUNCTIONS: roles + user_roles
-- ============================================================

-- ----------------------------------------------------------------
-- Assign a role to a user
-- assigned_by is the user_id of the admin performing the action
-- ----------------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_assign_role(
    p_user_id     BIGINT,
    p_role_id     BIGINT,
    p_assigned_by BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM users WHERE id = p_user_id) THEN
        RAISE EXCEPTION 'USER_NOT_FOUND: %', p_user_id;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM roles WHERE id = p_role_id) THEN
        RAISE EXCEPTION 'ROLE_NOT_FOUND: %', p_role_id;
    END IF;

    IF EXISTS (
        SELECT 1 FROM user_roles
        WHERE user_id = p_user_id AND role_id = p_role_id
    ) THEN
        RAISE EXCEPTION 'ROLE_ALREADY_ASSIGNED: user=%, role=%', p_user_id, p_role_id;
    END IF;

    INSERT INTO user_roles (user_id, role_id, assigned_by)
    VALUES (p_user_id, p_role_id, p_assigned_by);
END;
$$;

-- ----------------------------------------------------------------
-- Revoke a role from a user
-- ----------------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_revoke_role(
    p_user_id BIGINT,
    p_role_id BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM user_roles
        WHERE user_id = p_user_id AND role_id = p_role_id
    ) THEN
        RAISE EXCEPTION 'ROLE_NOT_ASSIGNED: user=%, role=%', p_user_id, p_role_id;
    END IF;

    DELETE FROM user_roles
    WHERE user_id = p_user_id AND role_id = p_role_id;
END;
$$;

-- ----------------------------------------------------------------
-- Replace all roles for a user in a single transaction
-- Useful when an admin reassigns a user's full role set at once
-- ----------------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_replace_user_roles(
    p_user_id     BIGINT,
    p_role_ids    BIGINT[],
    p_assigned_by BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM users WHERE id = p_user_id) THEN
        RAISE EXCEPTION 'USER_NOT_FOUND: %', p_user_id;
    END IF;

    -- Remove all existing roles for this user
    DELETE FROM user_roles WHERE user_id = p_user_id;

    -- Insert the new role set
    INSERT INTO user_roles (user_id, role_id, assigned_by)
    SELECT p_user_id, unnest(p_role_ids), p_assigned_by;
END;
$$;

-- ----------------------------------------------------------------
-- Get all roles
-- ----------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_get_all_roles()
RETURNS TABLE (
    id        BIGINT,
    role_name VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT r.id, r.role_name
    FROM roles r
    ORDER BY r.role_name;
END;
$$;

-- ----------------------------------------------------------------
-- Get a role by name
-- ----------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_get_role_by_name(p_role_name VARCHAR)
RETURNS TABLE (
    id        BIGINT,
    role_name VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT r.id, r.role_name
    FROM roles r
    WHERE r.role_name = p_role_name;
END;
$$;

-- ----------------------------------------------------------------
-- Get all roles assigned to a specific user
-- ----------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_get_roles_for_user(p_user_id BIGINT)
RETURNS TABLE (
    role_id     BIGINT,
    role_name   VARCHAR,
    assigned_at TIMESTAMPTZ,
    assigned_by BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT r.id, r.role_name, ur.assigned_at, ur.assigned_by
    FROM user_roles ur
    JOIN roles r ON r.id = ur.role_id
    WHERE ur.user_id = p_user_id
    ORDER BY r.role_name;
END;
$$;