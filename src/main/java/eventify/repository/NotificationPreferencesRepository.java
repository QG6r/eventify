package eventify.repository;

import eventify.entity.NotificationPreferences;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationPreferencesRepository extends JpaRepository<NotificationPreferences, Long> {

    Optional<NotificationPreferences> findByUserId(Long userId);

    // Бот ищет пользователя по разовому коду привязки при команде /start.
    Optional<NotificationPreferences> findByTelegramLinkCode(String telegramLinkCode);
}