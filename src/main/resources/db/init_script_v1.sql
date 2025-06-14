DROP TABLE IF EXISTS view_event        CASCADE;
DROP TABLE IF EXISTS rating_event      CASCADE;
DROP TABLE IF EXISTS movie_genre       CASCADE;
DROP TABLE IF EXISTS movie             CASCADE;
DROP TABLE IF EXISTS app_user          CASCADE;



CREATE TABLE app_user (
    id        BIGINT PRIMARY KEY,
    username  VARCHAR(100) NOT NULL
);

CREATE TABLE movie (
    id     BIGINT PRIMARY KEY,
    title  VARCHAR(255) NOT NULL
);


CREATE TABLE movie_genre (
    movie_id BIGINT REFERENCES movie(id) ON DELETE CASCADE,
    genre    VARCHAR(50) NOT NULL,
    PRIMARY KEY (movie_id, genre)
);



CREATE TYPE rating_source AS ENUM ('EXPLICIT', 'IMPLICIT');
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'rating_source') THEN
    CREATE TYPE rating_source AS ENUM ('EXPLICIT', 'IMPLICIT');
  END IF;
END$$;

CREATE TABLE rating_event (
    id        BIGSERIAL PRIMARY KEY,
    user_id   BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    movie_id  BIGINT NOT NULL REFERENCES movie(id)  ON DELETE CASCADE,
    rating    INT    NOT NULL CHECK (rating BETWEEN 1 AND 5),
    source    rating_source NOT NULL,
    ts        TIMESTAMP WITH TIME ZONE DEFAULT now(),
    UNIQUE (user_id, movie_id)
);

CREATE TABLE view_event (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    movie_id     BIGINT NOT NULL REFERENCES movie(id)  ON DELETE CASCADE,
    view_percent INT    NOT NULL CHECK (view_percent BETWEEN 0 AND 100),
    ts           TIMESTAMP WITH TIME ZONE DEFAULT now()
);



CREATE INDEX IF NOT EXISTS idx_view_event_user  ON view_event(user_id);
CREATE INDEX IF NOT EXISTS idx_rating_event_usr ON rating_event(user_id);
CREATE INDEX IF NOT EXISTS idx_movie_genre_gen  ON movie_genre(genre);
