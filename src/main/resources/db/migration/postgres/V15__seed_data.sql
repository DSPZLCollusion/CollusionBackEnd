-- =============================================================================
-- seed.sql — Development seed data for API testing
-- =============================================================================
-- Usage:
--   psql -U postgres -d postgres -f seed.sql
--
-- Or against the dev Docker container (port 5433):
--   psql -h localhost -p 5433 -U postgres -d postgres -f seed.sql
--
-- Safe to re-run: all inserts are wrapped in a transaction and the script
-- clears existing seed data first (in FK-safe order) before re-inserting.
--
-- Passwords: all test users have the password  →  Password1!
-- BCrypt hash below was generated with strength 10.
-- =============================================================================

BEGIN;

-- ── 1. CLEAR existing data (FK-safe order: children before parents) ──────────

DELETE FROM on_campus_housing;
DELETE FROM off_campus_housing;
DELETE FROM user_roles;
DELETE FROM pnms;
DELETE FROM events;
DELETE FROM users
WHERE email IN (
                'admin@collusion.dev',
                'director@collusion.dev',
                'user@collusion.dev'
    );

-- ── 2. USERS ─────────────────────────────────────────────────────────────────
-- All passwords are:  Password1!
-- BCrypt hash (strength 10): $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi
--
-- Set it to a placeholder so the NOT NULL constraint is satisfied.

INSERT INTO users (first_name, last_name, email, password_hash)
VALUES
    ('Admin',    'User',    'admin@collusion.dev',     '$2y$10$IvSVkXdbAX1a4UBy7Og7KezGppcJVU0kdtOOfcsS4aSQUOoZCwceG'),
    ('Director', 'User',    'director@collusion.dev',  '$2y$10$IvSVkXdbAX1a4UBy7Og7KezGppcJVU0kdtOOfcsS4aSQUOoZCwceG'),
    ('Regular',  'User',    'user@collusion.dev',      '$2y$10$IvSVkXdbAX1a4UBy7Og7KezGppcJVU0kdtOOfcsS4aSQUOoZCwceG');

-- ── 3. USER_ROLES ─────────────────────────────────────────────────────────────
-- Roles were seeded by V3__create_table_roles.sql:  ADMIN=1, DIRECTOR=2, USER=3
-- assigned_by is nullable — left null for seed data (no actor at bootstrap).

INSERT INTO user_roles (user_id, role_id, assigned_at)
SELECT u.id, r.id, now()
FROM   users u, roles r
WHERE  u.email = 'admin@collusion.dev'    AND r.role_name = 'ADMIN';

INSERT INTO user_roles (user_id, role_id, assigned_at)
SELECT u.id, r.id, now()
FROM   users u, roles r
WHERE  u.email = 'director@collusion.dev' AND r.role_name = 'DIRECTOR';

INSERT INTO user_roles (user_id, role_id, assigned_at)
SELECT u.id, r.id, now()
FROM   users u, roles r
WHERE  u.email = 'user@collusion.dev'     AND r.role_name = 'USER';

-- ── 4. PNMS ───────────────────────────────────────────────────────────────────
-- Mix of:
--   • all class years (FRESHMAN → SUPER_SENIOR)
--   • all statuses (DELTA, SIGMA, PHI) + one with NULL status (not yet evaluated)
--   • both housing types (ON_CAMPUS, OFF_CAMPUS)

INSERT INTO pnms (first_name, last_name, year, status, housing_type)
VALUES
    -- On-campus PNMs
    ('James',    'Carter',   'FRESHMAN'::class_year,     'DELTA'::status,  'ON_CAMPUS'::housing_type),
    ('Olivia',   'Bennett',  'SOPHOMORE'::class_year,    'SIGMA'::status,  'ON_CAMPUS'::housing_type),
    ('Ethan',    'Morrison', 'JUNIOR'::class_year,       'PHI'::status,    'ON_CAMPUS'::housing_type),
    ('Sophia',   'Nguyen',   'SENIOR'::class_year,       'DELTA'::status,  'ON_CAMPUS'::housing_type),
    ('Marcus',   'Webb',     'SUPER_SENIOR'::class_year, 'SIGMA'::status,  'ON_CAMPUS'::housing_type),

    -- On-campus, status not yet assigned (NULL)
    ('Ava',      'Thornton', 'FRESHMAN'::class_year,     NULL,             'ON_CAMPUS'::housing_type),
    ('Liam',     'Foster',   'SOPHOMORE'::class_year,    NULL,             'ON_CAMPUS'::housing_type),

    -- Off-campus PNMs
    ('Emma',     'Sullivan', 'JUNIOR'::class_year,       'PHI'::status,    'OFF_CAMPUS'::housing_type),
    ('Noah',     'Rivera',   'SENIOR'::class_year,       'DELTA'::status,  'OFF_CAMPUS'::housing_type),
    ('Isabella', 'Chen',     'FRESHMAN'::class_year,     'SIGMA'::status,  'OFF_CAMPUS'::housing_type),

    -- Off-campus, status not yet assigned (NULL)
    ('Mason',    'Patel',    'SOPHOMORE'::class_year,    NULL,             'OFF_CAMPUS'::housing_type),
    ('Luna',     'Brooks',   'SUPER_SENIOR'::class_year, NULL,             'OFF_CAMPUS'::housing_type);

-- ── 5. ON_CAMPUS_HOUSING ──────────────────────────────────────────────────────
-- One row per on-campus PNM — every dorm represented at least once.

INSERT INTO on_campus_housing (pnm_id, dorm, room_number)
SELECT p.id, 'SPEED'::dorm, '101A'
FROM pnms p
WHERE p.first_name = 'James'
  AND p.last_name = 'Carter';

INSERT INTO on_campus_housing (pnm_id, dorm, room_number)
SELECT p.id, 'BSB'::dorm, '204'
FROM pnms p
WHERE p.first_name = 'Olivia'
  AND p.last_name = 'Bennett';

INSERT INTO on_campus_housing (pnm_id, dorm, room_number)
SELECT p.id, 'BLUMBERG'::dorm, '312B'
FROM pnms p
WHERE p.first_name = 'Ethan'
  AND p.last_name = 'Morrison';

INSERT INTO on_campus_housing (pnm_id, dorm, room_number)
SELECT p.id, 'MEES'::dorm, '118'
FROM pnms p
WHERE p.first_name = 'Sophia'
  AND p.last_name = 'Nguyen';

INSERT INTO on_campus_housing (pnm_id, dorm, room_number)
SELECT p.id, 'DEMING'::dorm, '220C'
FROM pnms p
WHERE p.first_name = 'Marcus'
  AND p.last_name = 'Webb';

INSERT INTO on_campus_housing (pnm_id, dorm, room_number)
SELECT p.id, 'LAKESIDE'::dorm, '405'
FROM pnms p
WHERE p.first_name = 'Ava'
  AND p.last_name = 'Thornton';

INSERT INTO on_campus_housing (pnm_id, dorm, room_number)
SELECT p.id, 'PERCOPO'::dorm, '309A'
FROM pnms p
WHERE p.first_name = 'Liam'
  AND p.last_name = 'Foster';

-- ── 6. OFF_CAMPUS_HOUSING ─────────────────────────────────────────────────────

INSERT INTO off_campus_housing (pnm_id, street_address, city, state, zip_code)
SELECT p.id, '842 Wabash Ave',     'Terre Haute', 'IN', '47807'
FROM   pnms p WHERE p.first_name = 'Emma'    AND p.last_name = 'Sullivan';

INSERT INTO off_campus_housing (pnm_id, street_address, city, state, zip_code)
SELECT p.id, '317 Cherry St',      'Terre Haute', 'IN', '47807'
FROM   pnms p WHERE p.first_name = 'Noah'    AND p.last_name = 'Rivera';

INSERT INTO off_campus_housing (pnm_id, street_address, city, state, zip_code)
SELECT p.id, '1204 Ohio St Apt 2', 'Terre Haute', 'IN', '47809'
FROM   pnms p WHERE p.first_name = 'Isabella'AND p.last_name = 'Chen';

INSERT INTO off_campus_housing (pnm_id, street_address, city, state, zip_code)
SELECT p.id, '56 N 6th St',        'Terre Haute', 'IN', '47807'
FROM   pnms p WHERE p.first_name = 'Mason'   AND p.last_name = 'Patel';

INSERT INTO off_campus_housing (pnm_id, street_address, city, state, zip_code)
SELECT p.id, '728 Poplar St',      'Terre Haute', 'IN', '47803'
FROM   pnms p WHERE p.first_name = 'Luna'    AND p.last_name = 'Brooks';

-- ── 7. EVENTS ─────────────────────────────────────────────────────────────────
-- Mix of past, current-week, and future events for testing date filtering.

INSERT INTO events (name, date)
VALUES
    ('Info Night',              CURRENT_DATE - INTERVAL '14 days'),
    ('Meet the Chapter',        CURRENT_DATE - INTERVAL '7 days'),
    ('Philanthropy Day',        CURRENT_DATE - INTERVAL '3 days'),
    ('Bid Day Prep',            CURRENT_DATE),
    ('Rush Kickoff',            CURRENT_DATE + INTERVAL '3 days'),
    ('Brotherhood Social',      CURRENT_DATE + INTERVAL '7 days'),
    ('Final Rush Event',        CURRENT_DATE + INTERVAL '14 days');

-- ── 8. VERIFY ─────────────────────────────────────────────────────────────────

DO $$
    DECLARE
        user_count    INT;
        pnm_count     INT;
        on_cam_count  INT;
        off_cam_count INT;
        event_count   INT;
    BEGIN
        SELECT COUNT(*) INTO user_count    FROM users;
        SELECT COUNT(*) INTO pnm_count     FROM pnms;
        SELECT COUNT(*) INTO on_cam_count  FROM on_campus_housing;
        SELECT COUNT(*) INTO off_cam_count FROM off_campus_housing;
        SELECT COUNT(*) INTO event_count   FROM events;

        RAISE NOTICE '=== Seed complete ===';
        RAISE NOTICE 'users:               %', user_count;
        RAISE NOTICE 'pnms:                %', pnm_count;
        RAISE NOTICE 'on_campus_housing:   %', on_cam_count;
        RAISE NOTICE 'off_campus_housing:  %', off_cam_count;
        RAISE NOTICE 'events:              %', event_count;
        RAISE NOTICE '';
        RAISE NOTICE 'Test credentials (all passwords: Password1!)';
        RAISE NOTICE '  ADMIN:    admin@collusion.dev';
        RAISE NOTICE '  DIRECTOR: director@collusion.dev';
        RAISE NOTICE '  USER:     user@collusion.dev';
    END $$;

COMMIT;