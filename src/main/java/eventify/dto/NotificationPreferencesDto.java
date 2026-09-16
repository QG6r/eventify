package eventify.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record NotificationPreferencesDto(
        Boolean notifyNewEvents,
        Boolean notifyUpcoming,
        @Min(1) @Max(24) Integer notifyBeforeHours
) {}