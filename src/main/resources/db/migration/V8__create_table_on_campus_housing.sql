CREATE TABLE on_campus_housing (
    pnm_id BIGINT PRIMARY KEY  references pnms,
    dorm dorm NOT NULL ,
    room_number VARCHAR(20) NOT NULL
);