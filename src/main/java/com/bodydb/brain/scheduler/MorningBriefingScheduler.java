package com.bodydb.brain.scheduler;

import com.bodydb.brain.service.BrainService;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class MorningBriefingScheduler {

    private static final Logger log = LoggerFactory.getLogger(MorningBriefingScheduler.class);
    private final BrainService brainService;

    public MorningBriefingScheduler(BrainService brainService) {
        this.brainService = brainService;
    }

    /** Runs every day at 08:00 (server local time) */
    @Scheduled(cron = "0 0 8 * * ?")
    void morningBriefing() {
        log.info("Triggering morning briefing...");
        try {
            brainService.generateAndSendMorningBriefing();
        } catch (Exception e) {
            log.error("Morning briefing failed: {}", e.getMessage(), e);
        }
    }
}
