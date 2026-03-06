DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users
(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name  VARCHAR(50) NOT NULL,
    email      VARCHAR(62) NOT NULL UNIQUE,
    password_salt VARCHAR NOT NULL,
    password_hash VARCHAR NOT NULL
);

CREATE INDEX idx_users_email      ON users (email);
