package com.bodydb.health.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.util.Map;

@Serdeable
@JsonIgnoreProperties(ignoreUnknown = true)
public record WorkoutEntry(
    String activityType,
    Double duration,
    Instant startDate,
    Instant endDate,
    String source,
    @Nullable Map<String, WorkoutStatistic> statistics
) {
}
