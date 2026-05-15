CREATE OR REPLACE PROCEDURE sp_create_user(
    p_first_name  VARCHAR,
    p_last_name   VARCHAR,
    p_email       VARCHAR,
    p_hash        VARCHAR,
    OUT p_user_id BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    IF EXISTS (SELECT 1 FROM users WHERE email = p_email) THEN
        RAISE EXCEPTION 'EMAIL_TAKEN: %', p_email;
    END IF;

    INSERT INTO users (first_name, last_name, email, password_hash)
    VALUES (p_first_name, p_last_name, p_email, p_hash)
    RETURNING id INTO p_user_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_update_user(
    p_user_id    BIGINT,
    p_first_name VARCHAR,
    p_last_name  VARCHAR,
    p_email      VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM users WHERE id = p_user_id) THEN
        RAISE EXCEPTION 'USER_NOT_FOUND: %', p_user_id;
    END IF;

    IF EXISTS (
        SELECT 1 FROM users
        WHERE email = p_email AND id != p_user_id
    ) THEN
        RAISE EXCEPTION 'EMAIL_TAKEN: %', p_email;
    END IF;

    UPDATE users
    SET first_name = p_first_name,
        last_name  = p_last_name,
        email      = p_email
    WHERE id = p_user_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_update_user_password(
    p_user_id BIGINT,
    p_hash    VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM users WHERE id = p_user_id) THEN
        RAISE EXCEPTION 'USER_NOT_FOUND: %', p_user_id;
    END IF;

    UPDATE users
    SET password_hash = p_hash
    WHERE id = p_user_id;
END;
$$;


CREATE OR REPLACE FUNCTION fn_get_user_by_id(p_user_id BIGINT)
RETURNS TABLE (
    id         BIGINT,
    first_name VARCHAR,
    last_name  VARCHAR,
    email      VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT u.id, u.first_name, u.last_name, u.email
    FROM users u
    WHERE u.id = p_user_id;
END;
$$;


CREATE OR REPLACE FUNCTION fn_get_user_by_email_for_auth(p_email VARCHAR)
RETURNS TABLE (
    id            BIGINT,
    first_name    VARCHAR,
    last_name     VARCHAR,
    email         VARCHAR,
    password_hash VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT u.id, u.first_name, u.last_name, u.email, u.password_hash
    FROM users u
    WHERE u.email = p_email;
END;
$$;

CREATE OR REPLACE FUNCTION fn_get_all_users()
RETURNS TABLE (
    id         BIGINT,
    first_name VARCHAR,
    last_name  VARCHAR,
    email      VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT u.id, u.first_name, u.last_name, u.email
    FROM users u
    ORDER BY u.last_name, u.first_name;
END;
$$;

CREATE OR REPLACE FUNCTION fn_get_all_users_with_roles()
RETURNS TABLE (
    user_id     BIGINT,
    first_name  VARCHAR,
    last_name   VARCHAR,
    email       VARCHAR,
    role_id     BIGINT,
    role_name   VARCHAR,
    assigned_at TIMESTAMPTZ,
    assigned_by BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT u.id, u.first_name, u.last_name, u.email,
           r.id, r.role_name,
           ur.assigned_at, ur.assigned_by
    FROM users u
    JOIN user_roles ur ON ur.user_id = u.id
    JOIN roles r       ON r.id = ur.role_id
    ORDER BY u.last_name, u.first_name, r.role_name;
END;
$$;

CREATE OR REPLACE FUNCTION fn_get_user_with_roles(p_user_id BIGINT)
RETURNS TABLE (
    user_id     BIGINT,
    first_name  VARCHAR,
    last_name   VARCHAR,
    email       VARCHAR,
    role_id     BIGINT,
    role_name   VARCHAR,
    assigned_at TIMESTAMPTZ,
    assigned_by BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT u.id, u.first_name, u.last_name, u.email,
           r.id, r.role_name,
           ur.assigned_at, ur.assigned_by
    FROM users u
    JOIN user_roles ur ON ur.user_id = u.id
    JOIN roles r       ON r.id = ur.role_id
    WHERE u.id = p_user_id
    ORDER BY r.role_name;
END;
$$;

CREATE OR REPLACE FUNCTION fn_get_user_roles_by_email(p_email VARCHAR)
RETURNS TABLE (
    user_id    BIGINT,
    first_name VARCHAR,
    last_name  VARCHAR,
    email      VARCHAR,
    role_name  VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT u.id, u.first_name, u.last_name, u.email,
           r.role_name
    FROM users u
    JOIN user_roles ur ON ur.user_id = u.id
    JOIN roles r       ON r.id = ur.role_id
    WHERE u.email = p_email
    ORDER BY r.role_name;
END;
$$;