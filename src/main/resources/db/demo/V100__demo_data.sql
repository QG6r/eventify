-- Optional demo data (loaded only when classpath:db/demo is added to spring.flyway.locations,
-- which docker-compose does by default). Dates are relative to the moment of the first start,
-- so the demo always shows upcoming events.

-- Demo user: demo@eventify.local / demo12345
INSERT INTO users (email, password_hash, role)
VALUES ('demo@eventify.local', '$2b$10$LKvvvfBihUHVHAB8vgrjA.E94b6fdqvg9zQ4mMKLr1871CH/npL1O', 'USER')
ON CONFLICT (email) DO NOTHING;

INSERT INTO notification_preferences (user_id)
SELECT id FROM users WHERE email = 'demo@eventify.local'
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO events (title, description, date_time, total_tickets) VALUES
    ('Java Meetup Yerevan',
     'Evening talks about Spring Boot, testing and clean architecture, followed by networking.',
     now() + interval '7 days', 80),
    ('Open Air Jazz Night',
     'Live jazz quartet in the city park. Bring a blanket and a friend.',
     now() + interval '10 days', 150),
    ('Startup Pitch Day',
     'Ten early-stage teams pitch to a panel of investors and mentors.',
     now() + interval '14 days', 120),
    ('Data & AI Conference',
     'A full-day conference on practical machine learning, data engineering and responsible AI.',
     now() + interval '21 days', 300),
    ('Board Games Evening',
     'Relaxed evening with 50+ board games. Beginners welcome.',
     now() + interval '5 days', 30),
    ('Last Few Seats Workshop',
     'Hands-on workshop with only a handful of seats left - a good case to try the booking limits.',
     now() + interval '12 days', 5);

-- One confirmed booking so that the demo user already has something in "My bookings".
INSERT INTO bookings (user_id, event_id, ticket_count, expiry_time, confirmed, timezone)
SELECT u.id, e.id, 2, now() + interval '30 days', true, 'Asia/Yerevan'
FROM users u, events e
WHERE u.email = 'demo@eventify.local' AND e.title = 'Java Meetup Yerevan';
