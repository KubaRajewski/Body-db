package com.bodydb.health.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Serdeable
@JsonIgnoreProperties(ignoreUnknown = true)
public record WorkoutsExportRequest(@Nullable List<WorkoutEntry> workouts) {
}
