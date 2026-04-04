DROP TABLE IF EXISTS on_campus_housing CASCADE;

CREATE TABLE on_campus_housing
(
    pnm_id      BIGINT      PRIMARY KEY REFERENCES pnms (id),
    dorm        VARCHAR(20) NOT NULL CHECK (dorm IN (
                                                     'Speed', 'BSB', 'Blumberg', 'Mees', 'Deming',
                                                     'Scharpenberg', 'Lakeside', 'Percopo',
                                                     'Apartments West', 'Apartments East', 'TBA'
        )),
    room_number VARCHAR(20) NOT NULL
);