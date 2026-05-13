package com.bodydb.brain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;
import java.util.List;

@Serdeable
public record AnthropicRequest(
    String model,
    @JsonProperty("max_tokens") int maxTokens,
    String system,
    List<Message> messages
) {
    @Serdeable
    public record Message(String role, String content) {}
}
