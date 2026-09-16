package eventify.service;

import eventify.dto.AuthResponse;
import eventify.dto.LoginRequest;
import eventify.dto.RegisterRequest;
import eventify.entity.Role;
import eventify.entity.User;
import eventify.exception.AuthenticationException;
import eventify.exception.ConflictException;
import eventify.repository.UserRepository;
import eventify.security.JwtService;
import eventify.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void register_success() {
        RegisterRequest request = new RegisterRequest("new@test.com", "password123");
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.role()).isEqualTo(Role.USER);
    }

    @Test
    void register_duplicateEmail_throwsConflict() {
        RegisterRequest request = new RegisterRequest("exists@test.com", "password123");
        when(userRepository.existsByEmail("exists@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest("user@test.com", "password123");
        User user = User.builder()
                .id(1L).email("user@test.com").passwordHash("hashed").role(Role.USER)
                .build();
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.role()).isEqualTo(Role.USER);
    }

    @Test
    void login_wrongPassword_throwsAuthentication() {
        LoginRequest request = new LoginRequest("user@test.com", "wrong");
        User user = User.builder()
                .id(1L).email("user@test.com").passwordHash("hashed").role(Role.USER)
                .build();
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void login_userNotFound_throwsAuthentication() {
        LoginRequest request = new LoginRequest("ghost@test.com", "password123");
        when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AuthenticationException.class);
    }
}