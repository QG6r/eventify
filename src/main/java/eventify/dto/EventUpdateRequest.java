package eventify.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record EventUpdateRequest(
        @Size(max = 100) String title,
        String description,
        OffsetDateTime dateTime,
        @Min(1) Integer totalTickets,
        String coverUrl
) {}