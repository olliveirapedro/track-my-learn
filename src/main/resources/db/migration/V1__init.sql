CREATE TABLE subject (
                         id                  BIGSERIAL PRIMARY KEY,
                         name                VARCHAR(100) NOT NULL UNIQUE,
                         weekly_goal_minutes INT
);

CREATE TABLE study_session (
                               id               BIGSERIAL PRIMARY KEY,
                               subject_id       BIGINT NOT NULL REFERENCES subject(id),
                               topic            VARCHAR(150) NOT NULL,
                               started_at       TIMESTAMP NOT NULL,
                               duration_minutes INT NOT NULL CHECK (duration_minutes > 0),
                               notes            TEXT
);