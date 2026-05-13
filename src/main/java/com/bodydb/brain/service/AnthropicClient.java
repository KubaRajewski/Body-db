package com.bodydb.brain.service;

import com.bodydb.brain.dto.AnthropicRequest;
import com.bodydb.brain.dto.AnthropicResponse;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Singleton
public class AnthropicClient {

    private static final Logger log = LoggerFactory.getLogger(AnthropicClient.class);

    private final HttpClient client;
    private final String apiKey;
    private final String model;
    private final String apiVersion;

    public AnthropicClient(
        @Client("${anthropic.base-url}") HttpClient client,
        @Value("${anthropic.api-key}") String apiKey,
        @Value("${anthropic.model:claude-sonnet-4-6}") String model,
        @Value("${anthropic.version:2023-06-01}") String apiVersion
    ) {
        this.client = client;
        this.apiKey = apiKey;
        this.model = model;
        this.apiVersion = apiVersion;
    }

    public String complete(String systemPrompt, String userMessage) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("ANTHROPIC_API_KEY not configured — skipping Claude call");
            return "[Claude not configured]";
        }

        AnthropicRequest body = new AnthropicRequest(
            model,
            2048,
            systemPrompt,
            List.of(new AnthropicRequest.Message("user", userMessage))
        );

        HttpRequest<AnthropicRequest> request = HttpRequest.POST("/v1/messages", body)
            .contentType(MediaType.APPLICATION_JSON_TYPE)
            .header("x-api-key", apiKey)
            .header("anthropic-version", apiVersion);

        try {
            AnthropicResponse response = client.toBlocking()
                .retrieve(request, AnthropicResponse.class);
            return response.text();
        } catch (Exception e) {
            log.error("Anthropic API error: {}", e.getMessage(), e);
            return "[Error calling Claude: " + e.getMessage() + "]";
        }
    }
}
