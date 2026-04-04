-- ============================================================
-- V14__seed_data.sql
-- Dummy seeding data for development and testing
-- ============================================================

-- ----------------------------------------------------------------
-- USERS (passwords are BCrypt of 'Password1!' + salt — dev only)
-- ----------------------------------------------------------------
INSERT INTO users (first_name, last_name, email, password_salt, password_hash) VALUES
                                                                                   ('James',   'Carter',   'james.carter@collusion.dev',   'c2FsdDAwMDAwMDAwMDA=', '$2a$10$devhashplaceholderADMIN111111111111111111111111111111'),
                                                                                   ('Olivia',  'Bennett',  'olivia.bennett@collusion.dev', 'c2FsdDAwMDAwMDAwMDE=', '$2a$10$devhashplaceholderDIREC111111111111111111111111111111'),
                                                                                   ('Marcus',  'Webb',     'marcus.webb@collusion.dev',    'c2FsdDAwMDAwMDAwMDI=', '$2a$10$devhashplaceholderUSER1111111111111111111111111111111'),
                                                                                   ('Sophia',  'Nguyen',   'sophia.nguyen@collusion.dev',  'c2FsdDAwMDAwMDAwMDM=', '$2a$10$devhashplaceholderUSER2222222222222222222222222222222'),
                                                                                   ('Ethan',   'Brooks',   'ethan.brooks@collusion.dev',   'c2FsdDAwMDAwMDAwMDQ=', '$2a$10$devhashplaceholderUSER3333333333333333333333333333333');

-- ----------------------------------------------------------------
-- USER ROLES
-- assigned_by NULL = system seeded
-- ----------------------------------------------------------------
INSERT INTO user_roles (user_id, role_id, assigned_by) VALUES
                                                           (1, (SELECT id FROM roles WHERE role_name = 'ADMIN'),    NULL),
                                                           (2, (SELECT id FROM roles WHERE role_name = 'DIRECTOR'), NULL),
                                                           (3, (SELECT id FROM roles WHERE role_name = 'USER'),     NULL),
                                                           (4, (SELECT id FROM roles WHERE role_name = 'USER'),     NULL),
                                                           (5, (SELECT id FROM roles WHERE role_name = 'USER'),     NULL);

-- ----------------------------------------------------------------
-- PNMs
-- ----------------------------------------------------------------
INSERT INTO pnms (first_name, last_name, year, status, housing_type) VALUES
                                                                         ('Liam',      'Foster',    'Freshman',    'delta', 'on_campus'),
                                                                         ('Emma',      'Hayes',     'Sophomore',   'sigma', 'on_campus'),
                                                                         ('Noah',      'Patel',     'Junior',      'phi',   'off_campus'),
                                                                         ('Ava',       'Morrison',  'Freshman',    'delta', 'on_campus'),
                                                                         ('William',   'Chen',      'Senior',      'sigma', 'off_campus'),
                                                                         ('Isabella',  'Torres',    'Sophomore',   NULL,    'on_campus'),
                                                                         ('James',     'Kim',       'Freshman',    'delta', 'on_campus'),
                                                                         ('Mia',       'Robinson',  'Junior',      'phi',   'off_campus'),
                                                                         ('Benjamin',  'Scott',     'Senior',      NULL,    'on_campus'),
                                                                         ('Charlotte', 'Adams',     'Freshman',    'sigma', 'off_campus'),
                                                                         ('Lucas',     'Rivera',    'Sophomore',   'delta', 'on_campus'),
                                                                         ('Amelia',    'Walker',    'Junior',      'sigma', 'on_campus'),
                                                                         ('Mason',     'Hall',      'Super Senior','phi',   'off_campus'),
                                                                         ('Harper',    'White',     'Freshman',    NULL,    'on_campus'),
                                                                         ('Elijah',    'Martin',    'Sophomore',   'delta', 'off_campus');

-- ----------------------------------------------------------------
-- ON CAMPUS HOUSING
-- Matches pnms with housing_type = on_campus
-- ----------------------------------------------------------------
INSERT INTO on_campus_housing (pnm_id, dorm, room_number)
SELECT p.id, vals.dorm::dorm, vals.room
FROM pnms p
         JOIN (VALUES
                   ('Liam',     'Foster',    'Speed',            '204'),
                   ('Emma',     'Hayes',     'Blumberg',         '310'),
                   ('Ava',      'Morrison',  'Mees',             '115'),
                   ('Isabella', 'Torres',    'BSB',              '422'),
                   ('James',    'Kim',       'Deming',           '308'),
                   ('Benjamin', 'Scott',     'Lakeside',         '201'),
                   ('Lucas',    'Rivera',    'Scharpenberg',     '109'),
                   ('Amelia',   'Walker',    'Percopo',          '317'),
                   ('Harper',   'White',     'Apartments East',  '5B')
) AS vals(first_name, last_name, dorm, room)
              ON p.first_name = vals.first_name AND p.last_name = vals.last_name;

-- ----------------------------------------------------------------
-- OFF CAMPUS HOUSING
-- Matches pnms with housing_type = off_campus
-- ----------------------------------------------------------------
INSERT INTO off_campus_housing (pnm_id, street_address, city, state, zip_code)
SELECT p.id, vals.street, vals.city, vals.state, vals.zip
FROM pnms p
         JOIN (VALUES
                   ('Noah',      'Patel',    '742 Evergreen Terrace',  'Terre Haute', 'IN', '47803'),
                   ('William',   'Chen',     '1428 Elm Street',        'Terre Haute', 'IN', '47807'),
                   ('Mia',       'Robinson', '221B Baker Avenue',      'Terre Haute', 'IN', '47804'),
                   ('Charlotte', 'Adams',    '550 Wabash Ave Apt 3',   'Terre Haute', 'IN', '47807'),
                   ('Mason',     'Hall',     '1600 Ohio Blvd',         'Terre Haute', 'IN', '47803'),
                   ('Elijah',    'Martin',   '88 Poplar Street',       'Terre Haute', 'IN', '47809')
) AS vals(first_name, last_name, street, city, state, zip)
              ON p.first_name = vals.first_name AND p.last_name = vals.last_name;

-- ----------------------------------------------------------------
-- EVENTS
-- Mix of past, current, and upcoming events
-- ----------------------------------------------------------------
INSERT INTO events (name, date) VALUES
                                    ('Fall Rush Kickoff BBQ',        '2025-08-25'),
                                    ('Meet the Brothers Night',      '2025-09-02'),
                                    ('Bid Day',                      '2025-09-10'),
                                    ('Philanthropy 5K Run',          '2025-09-20'),
                                    ('Game Day Tailgate',            '2025-10-04'),
                                    ('Fall Formal',                  '2025-11-15'),
                                    ('Spring Rush Info Session',     '2026-01-18'),
                                    ('Spring Social Mixer',          '2026-02-08'),
                                    ('Leadership Workshop',          '2026-02-22'),
                                    ('Spring Bid Day',               '2026-03-08');