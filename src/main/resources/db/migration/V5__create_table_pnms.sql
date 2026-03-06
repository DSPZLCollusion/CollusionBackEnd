CREATE TABLE pnms
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name   VARCHAR(50)  NOT NULL,
    last_name    VARCHAR(50)  NOT NULL,
    year         class_year   NOT NULL,
    status       status,
    housing_type housing_type NOT NULL
);