package eventify.repository;

import eventify.entity.NotificationPreferences;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationPreferencesRepository extends JpaRepository<NotificationPreferences, Long> {

    Optional<NotificationPreferences> findByUserId(Long userId);

    // The bot looks up a user by a one-time link code when it receives the /start command.
    Optional<NotificationPreferences> findByTelegramLinkCode(String telegramLinkCode);
}