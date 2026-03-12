-- ============================================================
-- STORED PROCEDURES & FUNCTIONS: on_campus_housing + off_campus_housing
-- ============================================================

-- ----------------------------------------------------------------
-- Upsert on-campus housing for a PNM
-- Inserts if no record exists, updates if one does
-- Also clears any off_campus_housing record since housing_type
-- can only be one or the other
-- ----------------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_upsert_on_campus_housing(
    p_pnm_id      BIGINT,
    p_dorm        dorm,
    p_room_number VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pnms WHERE id = p_pnm_id) THEN
        RAISE EXCEPTION 'PNM_NOT_FOUND: %', p_pnm_id;
    END IF;

    -- Remove any existing off-campus record
    DELETE FROM off_campus_housing WHERE pnm_id = p_pnm_id;

    -- Upsert on-campus record
    INSERT INTO on_campus_housing (pnm_id, dorm, room_number)
    VALUES (p_pnm_id, p_dorm, p_room_number)
    ON CONFLICT (pnm_id) DO UPDATE
        SET dorm        = EXCLUDED.dorm,
            room_number = EXCLUDED.room_number;

    -- Keep housing_type in sync on the pnms row
    UPDATE pnms SET housing_type = 'on_campus' WHERE id = p_pnm_id;
END;
$$;

-- ----------------------------------------------------------------
-- Upsert off-campus housing for a PNM
-- Inserts if no record exists, updates if one does
-- Also clears any on_campus_housing record
-- ----------------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_upsert_off_campus_housing(
    p_pnm_id       BIGINT,
    p_street       VARCHAR,
    p_city         VARCHAR,
    p_state        VARCHAR,
    p_zip_code     VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pnms WHERE id = p_pnm_id) THEN
        RAISE EXCEPTION 'PNM_NOT_FOUND: %', p_pnm_id;
    END IF;

    -- Remove any existing on-campus record
    DELETE FROM on_campus_housing WHERE pnm_id = p_pnm_id;

    -- Upsert off-campus record
    INSERT INTO off_campus_housing (pnm_id, street_address, city, state, zip_code)
    VALUES (p_pnm_id, p_street, p_city, p_state, p_zip_code)
    ON CONFLICT (pnm_id) DO UPDATE
        SET street_address = EXCLUDED.street_address,
            city           = EXCLUDED.city,
            state          = EXCLUDED.state,
            zip_code       = EXCLUDED.zip_code;

    -- Keep housing_type in sync on the pnms row
    UPDATE pnms SET housing_type = 'off_campus' WHERE id = p_pnm_id;
END;
$$;

-- ----------------------------------------------------------------
-- Get on-campus housing for a PNM
-- ----------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_get_on_campus_housing(p_pnm_id BIGINT)
RETURNS TABLE (
    pnm_id      BIGINT,
    dorm        dorm,
    room_number VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT h.pnm_id, h.dorm, h.room_number
    FROM on_campus_housing h
    WHERE h.pnm_id = p_pnm_id;
END;
$$;

-- ----------------------------------------------------------------
-- Get off-campus housing for a PNM
-- ----------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_get_off_campus_housing(p_pnm_id BIGINT)
RETURNS TABLE (
    pnm_id         BIGINT,
    street_address VARCHAR,
    city           VARCHAR,
    state          VARCHAR,
    zip_code       VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT h.pnm_id, h.street_address, h.city, h.state, h.zip_code
    FROM off_campus_housing h
    WHERE h.pnm_id = p_pnm_id;
END;
$$;