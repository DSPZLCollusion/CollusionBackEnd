DROP TABLE IF EXISTS user_roles CASCADE;

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL ,
    role_id BIGINT NOT NULL,
    assigned_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    assigned_by BIGINT        NULL,

    PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_user
        FOREIGN KEY(user_id)
            REFERENCES users(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_role
        FOREIGN KEY(role_id)
            REFERENCES roles(id)
            ON DELETE CASCADE
)