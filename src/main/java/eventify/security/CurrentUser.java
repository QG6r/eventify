package eventify.security;

import eventify.entity.Role;
import eventify.exception.AuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    public Long getId() {
        return details().getId();
    }

    public boolean isAdmin() {
        return Role.ADMIN.name().equals(details().getRole());
    }

    private AppUserDetails details() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AppUserDetails userDetails)) {
            throw new AuthenticationException("Пользователь не аутентифицирован");
        }
        return userDetails;
    }
}