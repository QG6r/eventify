CREATE TABLE bookings (
                          id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          user_id      BIGINT      NOT NULL,
                          event_id     BIGINT      NOT NULL,
                          ticket_count INTEGER     NOT NULL,
                          created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
                          expiry_time  TIMESTAMPTZ NOT NULL,
                          confirmed    BOOLEAN     NOT NULL DEFAULT false,
                          timezone     VARCHAR(64),
                          CONSTRAINT fk_booking_user FOREIGN KEY (user_id)
                              REFERENCES users (id) ON DELETE CASCADE,
                          CONSTRAINT fk_booking_event FOREIGN KEY (event_id)
                              REFERENCES events (id) ON DELETE CASCADE,
                          CONSTRAINT chk_booking_ticket_count CHECK (ticket_count >= 1)
);

CREATE INDEX idx_bookings_user  ON bookings (user_id);
CREATE INDEX idx_bookings_event ON bookings (event_id);
CREATE INDEX idx_bookings_confirmed ON bookings (confirmed);