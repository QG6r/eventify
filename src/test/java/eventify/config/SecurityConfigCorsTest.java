package eventify.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigCorsTest {

    private final SecurityConfig securityConfig = new SecurityConfig(null, null, null);

    @Test
    void allowsConfiguredFrontendOriginWithAuthorizationHeader() {
        CorsConfigurationSource source = securityConfig.corsConfigurationSource(List.of("http://localhost:3000"));

        CorsConfiguration config = source.getCorsConfiguration(new MockHttpServletRequest("GET", "/events"));

        assertThat(config).isNotNull();
        assertThat(config.checkOrigin("http://localhost:3000")).isEqualTo("http://localhost:3000");
        assertThat(config.checkHeaders(List.of("Authorization", "Content-Type"))).hasSize(2);
        assertThat(config.checkHttpMethod(org.springframework.http.HttpMethod.PUT)).isNotNull();
    }

    @Test
    void rejectsUnknownOrigin() {
        CorsConfigurationSource source = securityConfig.corsConfigurationSource(List.of("http://localhost:3000"));

        CorsConfiguration config = source.getCorsConfiguration(new MockHttpServletRequest("GET", "/events"));

        assertThat(config.checkOrigin("http://evil.example")).isNull();
    }
}
