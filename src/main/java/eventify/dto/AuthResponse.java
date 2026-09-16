package eventify.dto;

import eventify.entity.Role;

public record AuthResponse(
        String token,
        Role role
) {}