package eventify.service;

import eventify.dto.NotificationPreferencesDto;
import eventify.entity.NotificationPreferences;
import eventify.exception.NotFoundException;
import eventify.repository.NotificationPreferencesRepository;
import eventify.repository.UserRepository;
import eventify.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationPreferencesRepository prefsRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void getReturnsDefaultsWhenUserHasNoSavedSettings() {
        when(prefsRepository.findByUserId(1L)).thenReturn(Optional.empty());

        NotificationPreferencesDto result = notificationService.get(1L);

        assertThat(result.notifyNewEvents()).isFalse();
        assertThat(result.notifyUpcoming()).isFalse();
        assertThat(result.notifyBeforeHours()).isEqualTo(1);
    }

    @Test
    void getReturnsSavedSettings() {
        NotificationPreferences prefs = new NotificationPreferences();
        prefs.setNotifyNewEvents(true);
        prefs.setNotifyUpcoming(true);
        prefs.setNotifyBeforeHours(6);
        when(prefsRepository.findByUserId(2L)).thenReturn(Optional.of(prefs));

        NotificationPreferencesDto result = notificationService.get(2L);

        assertThat(result.notifyNewEvents()).isTrue();
        assertThat(result.notifyUpcoming()).isTrue();
        assertThat(result.notifyBeforeHours()).isEqualTo(6);
    }

    @Test
    void deleteFailsWhenThereAreNoSettings() {
        when(prefsRepository.findByUserId(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.delete(3L)).isInstanceOf(NotFoundException.class);
    }
}
