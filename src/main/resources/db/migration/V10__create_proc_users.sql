CREATE OR REPLACE FUNCTION create_user(
    p_first_name VARCHAR,
    p_last_name VARCHAR,
    p_email VARCHAR,
    p_password_salt VARCHAR,
    p_password_hash VARCHAR
)
    RETURNS SETOF users
    LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
        INSERT INTO users (first_name, last_name, email, password_salt, password_hash)
            VALUES (p_first_name, p_last_name, p_email, p_password_salt, p_password_hash)
            RETURNING *;
END;
$$;

CREATE OR REPLACE FUNCTION update_user(
    p_id         BIGINT,
    p_email      VARCHAR DEFAULT NULL,
    p_first_name VARCHAR DEFAULT NULL,
    p_last_name  VARCHAR DEFAULT NULL
)
    RETURNS SETOF users
    LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
        UPDATE users
            SET
                email      = COALESCE(p_email,      email),
                first_name = COALESCE(p_first_name, first_name),
                last_name  = COALESCE(p_last_name,  last_name)
            WHERE id = p_id
            RETURNING *;
END;
$$;

CREATE OR REPLACE FUNCTION get_user_by_id(
    p_id BIGINT
)
    RETURNS SETOF users
    LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
        SELECT *
        FROM users
        WHERE id = p_id;
END;
$$;

CREATE OR REPLACE FUNCTION get_user_by_email(
    p_email VARCHAR
)
    RETURNS SETOF users
    LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
        SELECT *
        FROM users
        WHERE email = p_email;
END;
$$;