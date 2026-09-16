package eventify.dto;

import java.time.OffsetDateTime;

public record EventResponse(
        Long id,
        String title,
        String description,
        OffsetDateTime dateTime,
        Integer totalTickets,
        Integer availableTickets,
        String coverUrl
) {}