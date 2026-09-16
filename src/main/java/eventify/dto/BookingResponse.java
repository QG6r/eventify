package eventify.dto;

import java.time.OffsetDateTime;

public record BookingResponse(
        Long id,
        EventResponse event,
        String customerEmail,
        Integer ticketCount,
        OffsetDateTime createdAt,
        OffsetDateTime expiryTime,
        Boolean confirmed,
        String timezone
) {}