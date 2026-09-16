package eventify.controller;

import eventify.dto.NotificationPreferencesDto;
import eventify.security.CurrentUser;
import eventify.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "Настройки уведомлений пользователя")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final NotificationService notificationService;
    private final CurrentUser currentUser;

    @Operation(summary = "Получить настройки уведомлений")
    @GetMapping("/notifications")
    public NotificationPreferencesDto getNotifications() {
        return notificationService.get(currentUser.getId());
    }

    @Operation(summary = "Обновить настройки уведомлений")
    @PutMapping("/notifications")
    public NotificationPreferencesDto updateNotifications(@Valid @RequestBody NotificationPreferencesDto dto) {
        return notificationService.update(currentUser.getId(), dto);
    }

    @Operation(summary = "Отменить уведомления")
    @DeleteMapping("/notifications")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotifications() {
        notificationService.delete(currentUser.getId());
    }

    @Operation(summary = "Инициировать привязку Telegram")
    @PostMapping("/telegram/link")
    public String linkTelegram() {
        return notificationService.initTelegramLink(currentUser.getId());
    }
}