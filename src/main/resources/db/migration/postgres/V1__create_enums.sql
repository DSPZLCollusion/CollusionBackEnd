CREATE TYPE class_year AS ENUM (
    'FRESHMAN',
    'SOPHOMORE',
    'JUNIOR',
    'SENIOR',
    'SUPER_SENIOR'
    );

CREATE TYPE status AS ENUM (
    'DELTA',
    'SIGMA',
    'PHI'
    );

CREATE TYPE housing_type AS ENUM (
    'ON_CAMPUS',
    'OFF_CAMPUS'
    );

CREATE TYPE action_type AS ENUM (
    'info_change',
    'status_change',
    'comment_added',
    'comment_edited',
    'comment_deleted',
    'attendance_updated',
    'interest_added',
    'interest_removed',
    'contact_added'
    );

CREATE TYPE dorm AS ENUM (
    'SPEED',
    'BSB',
    'BLUMBERG',
    'MEES',
    'DEMING',
    'SCHARPENBERG',
    'LAKESIDE',
    'PERCOPO',
    'APARTMENTS WEST',
    'APARTMENTS EAST',
    'TBA'
    );