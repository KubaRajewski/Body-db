package com.bodydb.support;

import io.micronaut.test.support.TestPropertyProvider;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Map;

public abstract class PostgresIntegrationTestSupport implements TestPropertyProvider {

    protected static final String API_KEY = "test-key-123";

    private static final PostgreSQLContainer<?> POSTGRES =
        new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("bodydb")
            .withUsername("bodydb")
            .withPassword("bodydb");

    @Override
    public Map<String, String> getProperties() {
        if (!POSTGRES.isRunning()) {
            POSTGRES.start();
        }

        return Map.of(
            "datasources.default.url", POSTGRES.getJdbcUrl(),
            "datasources.default.username", POSTGRES.getUsername(),
            "datasources.default.password", POSTGRES.getPassword(),
            "bodydb.api-key", API_KEY
        );
    }
}
