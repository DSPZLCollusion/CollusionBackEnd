DROP TABLE IF EXISTS roles CASCADE;

CREATE TABLE roles (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    role_name VARCHAR(50)
);

INSERT INTO roles (role_name) VALUES
                                    ('ADMIN'),
                                    ('DIRECTOR'),
                                    ('USER')
