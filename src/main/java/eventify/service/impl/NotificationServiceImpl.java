package eventify.service.impl;

import eventify.dto.NotificationPreferencesDto;
import eventify.entity.NotificationPreferences;
import eventify.entity.User;
import eventify.exception.NotFoundException;
import eventify.repository.NotificationPreferencesRepository;
import eventify.repository.UserRepository;
import eventify.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationPreferencesRepository prefsRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public NotificationPreferencesDto get(Long currentUserId) {
        NotificationPreferences prefs = prefsRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new NotFoundException("Настройки уведомлений не найдены"));
        return toDto(prefs);
    }

    @Override
    @Transactional
    public NotificationPreferencesDto update(Long currentUserId, NotificationPreferencesDto dto) {
        NotificationPreferences prefs = prefsRepository.findByUserId(currentUserId)
                .orElseGet(() -> {
                    User user = userRepository.findById(currentUserId)
                            .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
                    NotificationPreferences np = new NotificationPreferences();
                    np.setUser(user);
                    return np;
                });

        if (dto.notifyNewEvents() != null) prefs.setNotifyNewEvents(dto.notifyNewEvents());
        if (dto.notifyUpcoming() != null) prefs.setNotifyUpcoming(dto.notifyUpcoming());
        if (dto.notifyBeforeHours() != null) prefs.setNotifyBeforeHours(dto.notifyBeforeHours());

        NotificationPreferences saved = prefsRepository.save(prefs);
        log.info("Обновлены настройки уведомлений пользователя id={}", currentUserId);
        return toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long currentUserId) {
        NotificationPreferences prefs = prefsRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new NotFoundException("Настройки уведомлений не найдены"));
        prefsRepository.delete(prefs);
        log.info("Удалены настройки уведомлений пользователя id={}", currentUserId);
    }

    @Override
    @Transactional
    public String initTelegramLink(Long currentUserId) {
        NotificationPreferences prefs = prefsRepository.findByUserId(currentUserId)
                .orElseGet(() -> {
                    User user = userRepository.findById(currentUserId)
                            .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
                    NotificationPreferences np = new NotificationPreferences();
                    np.setUser(user);
                    return np;
                });
        String code = UUID.randomUUID().toString().substring(0, 8);
        prefs.setTelegramLinkCode(code);
        prefsRepository.save(prefs);
        log.info("Сгенерирован код привязки Telegram для пользователя id={}", currentUserId);
        return code;
    }

    private NotificationPreferencesDto toDto(NotificationPreferences p) {
        return new NotificationPreferencesDto(
                p.isNotifyNewEvents(),
                p.isNotifyUpcoming(),
                p.getNotifyBeforeHours()
        );
    }
}