drop table IF EXISTS users, categories, locations, events, requests, compilations, compilation_events CASCADE;

create TABLE IF NOT EXISTS users
(
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name    VARCHAR(250)                            NOT NULL,
    email   VARCHAR(254)                            NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS categories
(
    category_id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name          VARCHAR(255),
    events_amount BIGINT,
    CONSTRAINT pk_categories PRIMARY KEY (category_id)
);

CREATE TABLE IF NOT EXISTS locations
(
    location_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    lat         REAL NOT NULL,
    lon         REAL NOT NULL
);

CREATE TABLE IF NOT EXISTS events
(
    event_id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    annotation         VARCHAR(2000)               NOT NULL,
    category_id        BIGINT                      NOT NULL REFERENCES categories (category_id),
    created_on         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    description        VARCHAR(7000)               NOT NULL,
    event_date         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    initiator_id       BIGINT                      NOT NULL REFERENCES users (user_id),
    location_id        BIGINT                      NOT NULL REFERENCES locations (location_id),
    paid               BOOLEAN,
    participant_limit  INT,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN,
    state              VARCHAR(15),
    title              VARCHAR(120)
);

CREATE TABLE IF NOT EXISTS requests
(
    request_id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    created      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    event_id     BIGINT REFERENCES events (event_id),
    requester_id BIGINT REFERENCES users (user_id),
    status       VARCHAR(15)
);

CREATE TABLE IF NOT EXISTS compilations
(
    compilation_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title          VARCHAR(50) NOT NULL,
    pinned         BOOLEAN
);

CREATE TABLE IF NOT EXISTS compilation_events
(
    compilation_id BIGINT REFERENCES compilations (compilation_id),
    event_id       BIGINT REFERENCES events (event_id)
);