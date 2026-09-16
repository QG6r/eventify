CREATE TABLE events (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title         VARCHAR(100) NOT NULL,
    description   TEXT,
    date_time     TIMESTAMPTZ  NOT NULL,
    total_tickets INTEGER      NOT NULL,
    cover_url     VARCHAR(512),
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT chk_events_total_tickets CHECK (total_tickets >= 1)
);

CREATE INDEX idx_events_date_time ON events (date_time);