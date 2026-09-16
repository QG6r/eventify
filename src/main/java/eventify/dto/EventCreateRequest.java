package eventify.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record EventCreateRequest(
        @NotBlank @Size(max = 100) String title,
        String description,
        String coverUrl,
        @NotNull OffsetDateTime dateTime,
        @NotNull @Min(1) Integer totalTickets
) {}