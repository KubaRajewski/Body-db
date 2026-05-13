package com.bodydb.importer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;
import java.util.Map;

/**
 * Maps the native Apple workouts export JSON from the iOS Shortcut.
 */
@Serdeable
@JsonIgnoreProperties(ignoreUnknown = true)
public record WorkoutExportDto(List<WorkoutEntry> workouts) {

    @Serdeable
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record WorkoutEntry(
        String activityType,
        double duration,          // seconds
        String startDate,         // ISO-8601 e.g. "2026-04-28T13:39:21Z"
        String endDate,
        String source,
        Map<String, StatValue> statistics
    ) {}

    @Serdeable
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record StatValue(
        Double sum,
        Double average,
        Double max,
        Double min,
        String unit
    ) {}
}
