package eventify.service;

import eventify.dto.NotificationPreferencesDto;

public interface NotificationService {

    NotificationPreferencesDto get(Long currentUserId);

    NotificationPreferencesDto update(Long currentUserId, NotificationPreferencesDto dto);

    void delete(Long currentUserId);

    String initTelegramLink(Long currentUserId);
}