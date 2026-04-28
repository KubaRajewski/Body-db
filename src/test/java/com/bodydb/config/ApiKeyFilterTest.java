package com.bodydb.config;

import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Property(name = "bodydb.api-key", value = "test-key-123")
class ApiKeyFilterTest implements TestPropertyProvider {

    @Controller("/api/test")
    static class ProtectedTestController {

        @Get("/protected")
        HttpResponse<Map<String, String>> protectedEndpoint() {
            return HttpResponse.ok(Map.of("status", "protected"));
        }
    }

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Override
    public Map<String, String> getProperties() {
        if (!postgres.isRunning()) {
            postgres.start();
        }

        return Map.of(
            "datasources.default.url", postgres.getJdbcUrl(),
            "datasources.default.username", postgres.getUsername(),
            "datasources.default.password", postgres.getPassword()
        );
    }

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void healthStatusRequiresNoApiKey() {
        var response = client.toBlocking().exchange(HttpRequest.GET("/api/health/status"));
        assertThat(response.getStatus().getCode()).isEqualTo(200);
    }

    @Test
    void missingApiKeyReturns401() {
        assertThatThrownBy(() ->
            client.toBlocking().exchange(HttpRequest.GET("/api/test/protected"))
        )
        .isInstanceOf(HttpClientResponseException.class)
        .satisfies(e -> {
            var responseException = (HttpClientResponseException) e;
            assertThat(responseException.getStatus().getCode()).isEqualTo(401);
            assertThat(responseException.getResponse().getBody(Map.class)).hasValue(Map.of("error", "invalid_api_key"));
        });
    }

    @Test
    void wrongApiKeyReturns401() {
        assertThatThrownBy(() ->
            client.toBlocking().exchange(
                HttpRequest.GET("/api/test/protected").header("X-API-Key", "wrong-key")
            )
        )
        .isInstanceOf(HttpClientResponseException.class)
        .satisfies(e -> assertThat(((HttpClientResponseException) e).getStatus().getCode()).isEqualTo(401));
    }

    @Test
    void correctApiKeyAllowsAccess() {
        var response = client.toBlocking().exchange(
            HttpRequest.GET("/api/test/protected").header("X-API-Key", "test-key-123"),
            Map.class
        );

        assertThat(response.getStatus().getCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo(Map.of("status", "protected"));
    }
}
