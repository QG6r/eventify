package eventify.dto;

import java.util.List;

public record ErrorResponse(
        String code,
        String level,
        String message,
        List<ErrorDetail> details
) {
    public ErrorResponse(String code, String level, String message) {
        this(code, level, message, null);
    }
}