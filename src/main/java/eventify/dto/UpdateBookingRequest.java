package eventify.dto;

import jakarta.validation.constraints.Min;

public record UpdateBookingRequest(
        @Min(1) Integer ticketCount
) {}