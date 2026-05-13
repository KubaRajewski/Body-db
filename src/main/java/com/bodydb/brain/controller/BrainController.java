package com.bodydb.brain.controller;

import com.bodydb.brain.service.BrainService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;

import java.util.Map;

@Controller("/brain")
public class BrainController {

    private final BrainService brainService;

    public BrainController(BrainService brainService) {
        this.brainService = brainService;
    }

    /** Manual trigger — POST /brain/briefing generates and sends the morning message immediately. */
    @Post("/briefing")
    public Map<String, String> triggerBriefing() {
        brainService.generateAndSendMorningBriefing();
        return Map.of("status", "briefing sent");
    }
}
