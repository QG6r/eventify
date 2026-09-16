package eventify.exception;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AccesDeniedException(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, message = "Пароль должен быть не короче 8 символов") String password
) {}