CREATE TABLE IF NOT EXISTS songs (
    id           BIGINT       PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    artist       VARCHAR(255) NOT NULL,
    album        VARCHAR(255) NOT NULL,
    duration     VARCHAR(255) NOT NULL,
    release_year VARCHAR(255) NOT NULL
);
