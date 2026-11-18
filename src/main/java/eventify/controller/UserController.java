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

@Tag(name = "User", description = "User notification settings")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final NotificationService notificationService;
    private final CurrentUser currentUser;

    @Operation(summary = "Get notification settings")
    @GetMapping("/notifications")
    public NotificationPreferencesDto getNotifications() {
        return notificationService.get(currentUser.getId());
    }

    @Operation(summary = "Update notification settings")
    @PutMapping("/notifications")
    public NotificationPreferencesDto updateNotifications(@Valid @RequestBody NotificationPreferencesDto dto) {
        return notificationService.update(currentUser.getId(), dto);
    }

    @Operation(summary = "Disable notifications")
    @DeleteMapping("/notifications")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotifications() {
        notificationService.delete(currentUser.getId());
    }

    @Operation(summary = "Start Telegram linking")
    @PostMapping("/telegram/link")
    public String linkTelegram() {
        return notificationService.initTelegramLink(currentUser.getId());
    }
}