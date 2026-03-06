CREATE or REPLACE FUNCTION get_role_by_id (
    p_id BIGINT
)
    RETURNS SETOF roles
    LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
        SELECT *
        FROM roles
        WHERE id = p_id;
END;
$$;

CREATE OR REPLACE FUNCTION get_all_roles()
    RETURNS SETOF roles
    LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
        SELECT *
        FROM roles
        ORDER BY role_name;
END;
$$;