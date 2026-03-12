-- ============================================================
-- STORED PROCEDURES & FUNCTIONS: events
-- ============================================================

-- ----------------------------------------------------------------
-- Create a new event
-- ----------------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_create_event(
    p_name     VARCHAR,
    p_date     DATE,
    OUT p_event_id BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    IF p_date IS NULL THEN
        RAISE EXCEPTION 'EVENT_DATE_REQUIRED';
    END IF;

    INSERT INTO events (name, date)
    VALUES (p_name, p_date)
    RETURNING id INTO p_event_id;
END;
$$;

-- ----------------------------------------------------------------
-- Update an event
-- ----------------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_update_event(
    p_event_id BIGINT,
    p_name     VARCHAR,
    p_date     DATE
)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM events WHERE id = p_event_id) THEN
        RAISE EXCEPTION 'EVENT_NOT_FOUND: %', p_event_id;
    END IF;

    UPDATE events
    SET name = p_name,
        date = p_date
    WHERE id = p_event_id;
END;
$$;

-- ----------------------------------------------------------------
-- Delete an event
-- ----------------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_delete_event(
    p_event_id BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM events WHERE id = p_event_id) THEN
        RAISE EXCEPTION 'EVENT_NOT_FOUND: %', p_event_id;
    END IF;

    DELETE FROM events WHERE id = p_event_id;
END;
$$;

-- ----------------------------------------------------------------
-- Get a single event by ID
-- ----------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_get_event_by_id(p_event_id BIGINT)
RETURNS TABLE (
    id   BIGINT,
    name VARCHAR,
    date DATE
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT e.id, e.name, e.date
    FROM events e
    WHERE e.id = p_event_id;
END;
$$;

-- ----------------------------------------------------------------
-- Get all events ordered by date ascending
-- ----------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_get_all_events()
RETURNS TABLE (
    id   BIGINT,
    name VARCHAR,
    date DATE
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT e.id, e.name, e.date
    FROM events e
    ORDER BY e.date ASC;
END;
$$;

-- ----------------------------------------------------------------
-- Get upcoming events (date >= today)
-- ----------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_get_upcoming_events()
RETURNS TABLE (
    id   BIGINT,
    name VARCHAR,
    date DATE
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT e.id, e.name, e.date
    FROM events e
    WHERE e.date >= CURRENT_DATE
    ORDER BY e.date ASC;
END;
$$;