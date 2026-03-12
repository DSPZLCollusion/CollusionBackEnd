-- ============================================================
-- STORED PROCEDURES & FUNCTIONS: pnms
-- ============================================================

-- ----------------------------------------------------------------
-- Create a new PNM
-- Returns the generated pnm id via OUT parameter
-- ----------------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_create_pnm(
    p_first_name   VARCHAR,
    p_last_name    VARCHAR,
    p_year         class_year,
    p_status       status,
    p_housing_type housing_type,
    OUT p_pnm_id   BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO pnms (first_name, last_name, year, status, housing_type)
    VALUES (p_first_name, p_last_name, p_year, p_status, p_housing_type)
    RETURNING id INTO p_pnm_id;
END;
$$;

-- ----------------------------------------------------------------
-- Update a PNM's general info
-- ----------------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_update_pnm(
    p_pnm_id       BIGINT,
    p_first_name   VARCHAR,
    p_last_name    VARCHAR,
    p_year         class_year,
    p_housing_type housing_type
)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pnms WHERE id = p_pnm_id) THEN
        RAISE EXCEPTION 'PNM_NOT_FOUND: %', p_pnm_id;
    END IF;

    UPDATE pnms
    SET first_name   = p_first_name,
        last_name    = p_last_name,
        year         = p_year,
        housing_type = p_housing_type
    WHERE id = p_pnm_id;
END;
$$;

-- ----------------------------------------------------------------
-- Update a PNM's status only
-- Kept separate so the activity log can record status_change
-- distinctly from info_change
-- ----------------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_update_pnm_status(
    p_pnm_id BIGINT,
    p_status status
)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pnms WHERE id = p_pnm_id) THEN
        RAISE EXCEPTION 'PNM_NOT_FOUND: %', p_pnm_id;
    END IF;

    UPDATE pnms
    SET status = p_status
    WHERE id = p_pnm_id;
END;
$$;

-- ----------------------------------------------------------------
-- Get a single PNM by ID
-- ----------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_get_pnm_by_id(p_pnm_id BIGINT)
RETURNS TABLE (
    id           BIGINT,
    first_name   VARCHAR,
    last_name    VARCHAR,
    year         class_year,
    status       status,
    housing_type housing_type
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT p.id, p.first_name, p.last_name, p.year, p.status, p.housing_type
    FROM pnms p
    WHERE p.id = p_pnm_id;
END;
$$;

-- ----------------------------------------------------------------
-- Get all PNMs — unfiltered
-- ----------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_get_all_pnms()
RETURNS TABLE (
    id           BIGINT,
    first_name   VARCHAR,
    last_name    VARCHAR,
    year         class_year,
    status       status,
    housing_type housing_type
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT p.id, p.first_name, p.last_name, p.year, p.status, p.housing_type
    FROM pnms p
    ORDER BY p.last_name, p.first_name;
END;
$$;

-- ----------------------------------------------------------------
-- Get PNMs filtered by status
-- ----------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_get_pnms_by_status(p_status status)
RETURNS TABLE (
    id           BIGINT,
    first_name   VARCHAR,
    last_name    VARCHAR,
    year         class_year,
    status       status,
    housing_type housing_type
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT p.id, p.first_name, p.last_name, p.year, p.status, p.housing_type
    FROM pnms p
    WHERE p.status = p_status
    ORDER BY p.last_name, p.first_name;
END;
$$;

-- ----------------------------------------------------------------
-- Get PNMs filtered by year
-- ----------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_get_pnms_by_year(p_year class_year)
RETURNS TABLE (
    id           BIGINT,
    first_name   VARCHAR,
    last_name    VARCHAR,
    year         class_year,
    status       status,
    housing_type housing_type
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT p.id, p.first_name, p.last_name, p.year, p.status, p.housing_type
    FROM pnms p
    WHERE p.year = p_year
    ORDER BY p.last_name, p.first_name;
END;
$$;

-- ----------------------------------------------------------------
-- Get a PNM with their full housing detail joined
-- Returns on_campus or off_campus columns — only one set
-- will be non-null depending on housing_type
-- ----------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_get_pnm_with_housing(p_pnm_id BIGINT)
RETURNS TABLE (
    id             BIGINT,
    first_name     VARCHAR,
    last_name      VARCHAR,
    year           class_year,
    status         status,
    housing_type   housing_type,
    -- on campus fields
    dorm           dorm,
    room_number    VARCHAR,
    -- off campus fields
    street_address VARCHAR,
    city           VARCHAR,
    state          VARCHAR,
    zip_code       VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT p.id, p.first_name, p.last_name, p.year, p.status, p.housing_type,
           oc.dorm, oc.room_number,
           offc.street_address, offc.city, offc.state, offc.zip_code
    FROM pnms p
    LEFT JOIN on_campus_housing  oc  ON oc.pnm_id  = p.id
    LEFT JOIN off_campus_housing offc ON offc.pnm_id = p.id
    WHERE p.id = p_pnm_id;
END;
$$;