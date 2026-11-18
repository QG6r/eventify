package eventify.security;

import eventify.config.JwtProperties;
import eventify.entity.Role;
import eventify.entity.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET = "test-secret-key-for-unit-tests-0123456789-abcdef";

    private JwtService jwtService;
    private User user;

    private static JwtService serviceWith(String secret, long expirationMs) {
        JwtProperties properties = new JwtProperties();
        properties.setSecret(secret);
        properties.setExpirationMs(expirationMs);
        return new JwtService(properties);
    }

    @BeforeEach
    void setUp() {
        jwtService = serviceWith(SECRET, 60_000L);
        user = User.builder().id(7L).email("admin@test.com").role(Role.ADMIN).build();
    }

    @Test
    void generatedTokenContainsEmailUserIdAndRole() {
        String token = jwtService.generateToken(user);

        assertThat(jwtService.extractEmail(token)).isEqualTo("admin@test.com");
        assertThat(jwtService.extractUserId(token)).isEqualTo(7L);
        assertThat(jwtService.extractRole(token)).isEqualTo(Role.ADMIN);
    }

    @Test
    void expiredTokenIsRejected() {
        JwtService shortLived = serviceWith(SECRET, -1_000L);
        String token = shortLived.generateToken(user);

        assertThatThrownBy(() -> shortLived.parse(token)).isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void tokenSignedWithAnotherSecretIsRejected() {
        JwtService other = serviceWith("another-secret-key-for-unit-tests-9876543210-zyxwvu", 60_000L);
        String foreignToken = other.generateToken(user);

        assertThatThrownBy(() -> jwtService.parse(foreignToken)).isInstanceOf(JwtException.class);
    }

    @Test
    void malformedTokenIsRejected() {
        assertThatThrownBy(() -> jwtService.parse("not-a-jwt")).isInstanceOf(JwtException.class);
    }
}
