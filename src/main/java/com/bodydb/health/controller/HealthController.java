package com.bodydb.health.controller;

import com.bodydb.health.dto.DailyHealthRequest;
import com.bodydb.health.dto.DailyHealthResponse;
import com.bodydb.health.dto.HealthExportRequest;
import com.bodydb.health.dto.ImportResult;
import com.bodydb.health.dto.WorkoutsExportRequest;
import com.bodydb.health.service.DailyHealthService;
import com.bodydb.health.service.HealthImportService;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import jakarta.validation.Valid;

import java.util.Map;

@Controller("/api/health")
public class HealthController {

    private final DailyHealthService dailyHealthService;
    private final HealthImportService healthImportService;

    public HealthController(DailyHealthService dailyHealthService, HealthImportService healthImportService) {
        this.dailyHealthService = dailyHealthService;
        this.healthImportService = healthImportService;
    }

    @Get("/status")
    public Map<String, String> status() {
        return Map.of("status", "UP");
    }

    @Post("/daily")
    public DailyHealthResponse upsertDailyHealth(@Valid @Body DailyHealthRequest request) {
        return dailyHealthService.upsert(request);
    }

    @Post("/import")
    public ImportResult importHealth(@Body HealthExportRequest request) {
        int days = healthImportService.importHealth(request);
        return new ImportResult(days, 0);
    }

    @Post("/workouts")
    public ImportResult importWorkouts(@Body WorkoutsExportRequest request) {
        int workouts = healthImportService.importWorkouts(request);
        return new ImportResult(0, workouts);
    }
}
