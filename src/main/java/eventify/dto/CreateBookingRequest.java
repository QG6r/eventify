package eventify.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateBookingRequest(
        @NotNull Long eventId,
        @NotNull @Min(1) Integer ticketCount
) {}