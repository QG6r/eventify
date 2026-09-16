CREATE TABLE notification_preferences (
                                          id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                          user_id             BIGINT  NOT NULL,
                                          notify_new_events   BOOLEAN NOT NULL DEFAULT false,
                                          notify_upcoming     BOOLEAN NOT NULL DEFAULT false,
                                          notify_before_hours INTEGER NOT NULL DEFAULT 1,
                                          telegram_chat_id    BIGINT,
                                          telegram_link_code  VARCHAR(64),
                                          CONSTRAINT uq_notif_user UNIQUE (user_id),
                                          CONSTRAINT fk_notif_user FOREIGN KEY (user_id)
                                              REFERENCES users (id) ON DELETE CASCADE,
                                          CONSTRAINT chk_notify_before_hours CHECK (notify_before_hours BETWEEN 1 AND 24)
);

CREATE INDEX idx_notif_link_code ON notification_preferences (telegram_link_code);