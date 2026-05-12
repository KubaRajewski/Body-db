package com.bodydb.health.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Serdeable
@JsonIgnoreProperties(ignoreUnknown = true)
public record HealthExportRequest(
    @Nullable List<MetricEntry> activeCalories,
    @Nullable List<MetricEntry> basalCalories,
    @Nullable List<MetricEntry> exerciseMinutes,
    @Nullable List<MetricEntry> flightsClimbed,
    @Nullable List<MetricEntry> heartRateVariability,
    @Nullable List<MetricEntry> oxygenSaturation,
    @Nullable List<MetricEntry> respiratoryRate,
    @Nullable List<MetricEntry> restingHeartRate,
    @Nullable List<SleepEntry> sleepTime,
    @Nullable List<MetricEntry> stepCount,
    @Nullable List<MetricEntry> vo2Max,
    @Nullable List<MetricEntry> walkingDistance,
    @Nullable List<MetricEntry> walkingHeartRate,
    @Nullable List<MetricEntry> wristTemperature
) {
}
