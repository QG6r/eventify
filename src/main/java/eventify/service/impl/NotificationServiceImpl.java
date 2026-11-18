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

    /** Values returned for a user who has not saved any notification settings yet (match the DB column defaults). */
    static final NotificationPreferencesDto DEFAULT_PREFERENCES = new NotificationPreferencesDto(false, false, 1);

    private final NotificationPreferencesRepository prefsRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public NotificationPreferencesDto get(Long currentUserId) {
        return prefsRepository.findByUserId(currentUserId)
                .map(this::toDto)
                .orElse(DEFAULT_PREFERENCES);
    }

    @Override
    @Transactional
    public NotificationPreferencesDto update(Long currentUserId, NotificationPreferencesDto dto) {
        NotificationPreferences prefs = prefsRepository.findByUserId(currentUserId)
                .orElseGet(() -> {
                    User user = userRepository.findById(currentUserId)
                            .orElseThrow(() -> new NotFoundException("User not found"));
                    NotificationPreferences np = new NotificationPreferences();
                    np.setUser(user);
                    return np;
                });

        if (dto.notifyNewEvents() != null) prefs.setNotifyNewEvents(dto.notifyNewEvents());
        if (dto.notifyUpcoming() != null) prefs.setNotifyUpcoming(dto.notifyUpcoming());
        if (dto.notifyBeforeHours() != null) prefs.setNotifyBeforeHours(dto.notifyBeforeHours());

        NotificationPreferences saved = prefsRepository.save(prefs);
        log.info("Notification settings updated for user id={}", currentUserId);
        return toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long currentUserId) {
        NotificationPreferences prefs = prefsRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new NotFoundException("Notification settings not found"));
        prefsRepository.delete(prefs);
        log.info("Notification settings deleted for user id={}", currentUserId);
    }

    @Override
    @Transactional
    public String initTelegramLink(Long currentUserId) {
        NotificationPreferences prefs = prefsRepository.findByUserId(currentUserId)
                .orElseGet(() -> {
                    User user = userRepository.findById(currentUserId)
                            .orElseThrow(() -> new NotFoundException("User not found"));
                    NotificationPreferences np = new NotificationPreferences();
                    np.setUser(user);
                    return np;
                });
        String code = UUID.randomUUID().toString().substring(0, 8);
        prefs.setTelegramLinkCode(code);
        prefsRepository.save(prefs);
        log.info("Telegram link code generated for user id={}", currentUserId);
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