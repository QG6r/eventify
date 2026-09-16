package eventify.dto;

public record ErrorDetail(
        String field,
        String message
) {}