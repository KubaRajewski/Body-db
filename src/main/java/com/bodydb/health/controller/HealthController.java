package com.bodydb.health.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

import java.util.Map;

@Controller("/api/health")
public class HealthController {

    @Get("/status")
    public Map<String, String> status() {
        return Map.of("status", "UP");
    }
}
