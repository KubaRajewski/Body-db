package com.bodydb.brain.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class TelegramService {

    private static final Logger log = LoggerFactory.getLogger(TelegramService.class);

    private final HttpClient client;
    private final String botToken;
    private final String chatId;

    public TelegramService(
        @Client("${telegram.base-url}") HttpClient client,
        @Value("${telegram.bot-token:}") String botToken,
        @Value("${telegram.chat-id:}") String chatId
    ) {
        this.client = client;
        this.botToken = botToken;
        this.chatId = chatId;
    }

    public void sendMessage(String text) {
        if (botToken == null || botToken.isBlank() || chatId == null || chatId.isBlank()) {
            log.warn("Telegram not configured (bot-token or chat-id missing) — message not sent:\n{}", text);
            return;
        }

        // Split long messages (Telegram limit 4096 chars)
        if (text.length() <= 4096) {
            send(text);
        } else {
            for (int i = 0; i < text.length(); i += 4096) {
                send(text.substring(i, Math.min(i + 4096, text.length())));
            }
        }
    }

    private void send(String text) {
        String url = "/bot" + botToken + "/sendMessage";
        SendMessageRequest body = new SendMessageRequest(chatId, text, "Markdown");
        HttpRequest<SendMessageRequest> req = HttpRequest.POST(url, body)
            .contentType(MediaType.APPLICATION_JSON_TYPE);
        try {
            client.toBlocking().exchange(req);
            log.info("Telegram message sent ({} chars)", text.length());
        } catch (Exception e) {
            log.error("Failed to send Telegram message: {}", e.getMessage(), e);
        }
    }

    @Serdeable
    private record SendMessageRequest(
        @JsonProperty("chat_id") String chatId,
        String text,
        @JsonProperty("parse_mode") String parseMode
    ) {}
}
