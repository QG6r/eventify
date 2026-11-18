-- V5 seeded a hash that does not match the documented demo password (admin12345),
-- and V6 contained a placeholder instead of a real hash. This migration sets the correct
-- BCrypt hash for the default admin and makes sure the admin has a notification-preferences row.
-- (V5/V6 are left untouched so that Flyway checksums of existing databases stay valid.)
UPDATE users
SET password_hash = '$2b$10$xlYlhYsnpDR3pEZYVm3Q6O7pudX2RqNW/MOXmSbghTQmvSvI1U7Lu'
WHERE email = 'admin@eventify.local';

INSERT INTO notification_preferences (user_id)
SELECT id FROM users WHERE email = 'admin@eventify.local'
ON CONFLICT (user_id) DO NOTHING;
