CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    login    TEXT   NOT NULL UNIQUE,
    password TEXT   NOT NULL,
    roles    TEXT[] NOT NULL DEFAULT '{}'
);

CREATE TABLE tasks
(
    id      TEXT PRIMARY KEY,
    user_id BIGINT REFERENCES users,
    status  BOOLEAN
)