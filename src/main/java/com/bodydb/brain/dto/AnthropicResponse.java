package com.bodydb.brain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.micronaut.serde.annotation.Serdeable;
import java.util.List;

@Serdeable
@JsonIgnoreProperties(ignoreUnknown = true)
public record AnthropicResponse(List<ContentBlock> content) {

    @Serdeable
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ContentBlock(String type, String text) {}

    public String text() {
        if (content == null || content.isEmpty()) return "";
        return content.stream()
            .filter(b -> "text".equals(b.type()))
            .map(ContentBlock::text)
            .findFirst().orElse("");
    }
}
