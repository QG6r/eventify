package eventify.service.impl;

import eventify.dto.AuthResponse;
import eventify.dto.LoginRequest;
import eventify.dto.RegisterRequest;
import eventify.entity.Role;
import eventify.entity.User;
import eventify.exception.AuthenticationException;
import eventify.exception.ConflictException;
import eventify.repository.UserRepository;
import eventify.security.JwtService;
import eventify.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("A user with this email already exists");
        }
        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();
        User saved = userRepository.save(user);
        log.info("User registered id={} email={}", saved.getId(), saved.getEmail());
        return new AuthResponse(jwtService.generateToken(saved), saved.getRole());
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new AuthenticationException("Invalid username or password");
        }
        log.info("User logged in id={}", user.getId());
        return new AuthResponse(jwtService.generateToken(user), user.getRole());
    }
}