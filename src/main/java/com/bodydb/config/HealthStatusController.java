package com.bodydb.config;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

import java.util.Map;

@Controller("/api/health")
public class HealthStatusController {

    @Get("/status")
    public Map<String, String> status() {
        return Map.of("status", "UP");
    }
}
