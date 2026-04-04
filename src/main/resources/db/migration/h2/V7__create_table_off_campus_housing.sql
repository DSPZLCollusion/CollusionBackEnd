DROP TABLE IF EXISTS off_campus_housing CASCADE;

CREATE TABLE off_campus_housing
(
    pnm_id         BIGINT       PRIMARY KEY REFERENCES pnms (id),
    street_address VARCHAR(255) NOT NULL,
    city           VARCHAR(100) NOT NULL,
    state          VARCHAR(50)  NOT NULL,
    zip_code       VARCHAR(20)  NOT NULL
);