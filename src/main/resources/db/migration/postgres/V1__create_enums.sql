CREATE TYPE class_year AS ENUM (
    'Freshman',
    'Sophomore',
    'Junior',
    'Senior',
    'Super Senior'
    );

CREATE TYPE status AS ENUM (
    'delta',
    'sigma',
    'phi'
    );

CREATE TYPE housing_type AS ENUM (
    'on_campus',
    'off_campus'
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

CREATE TYPE dorm as ENUM (
    'Speed',
    'BSB',
    'Blumberg',
    'Mees',
    'Deming',
    'Scharpenberg',
    'Lakeside',
    'Percopo',
    'Apartments West',
    'Apartments East',
    'TBA'
    )