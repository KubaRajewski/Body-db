package com.bodydb.importer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

/**
 * Maps the native Apple Health export JSON from the iOS Shortcut.
 * Arrays are per-day aggregates; sleepTime has nested stages.
 * All fields are @Nullable — the export may omit any metric.
 */
@Serdeable
@JsonIgnoreProperties(ignoreUnknown = true)
public record HealthExportDto(
    @Nullable List<DailyValue> activeCalories,
    @Nullable List<DailyValue> basalCalories,
    @Nullable List<DailyValue> stepCount,
    @Nullable List<DailyValue> walkingDistance,
    @Nullable List<DailyValue> exerciseMinutes,
    @Nullable List<DailyValue> restingHeartRate,
    @Nullable List<DailyValue> heartRateVariability,
    @Nullable List<DailyValue> oxygenSaturation,
    @Nullable List<DailyValue> respiratoryRate,
    @Nullable List<DailyValue> wristTemperature,
    @Nullable List<DailyValue> vo2Max,
    @Nullable List<DailyValue> flightsClimbed,
    @Nullable List<DailyValue> walkingHeartRate,
    @Nullable List<SleepValue> sleepTime,
    @Nullable List<DailyValue> dietaryEnergy,
    @Nullable List<DailyValue> dietaryProtein,
    @Nullable List<DailyValue> dietaryCarbohydrates,
    @Nullable List<DailyValue> dietaryFatTotal,
    @Nullable List<DailyValue> weight
) {

    @Serdeable
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DailyValue(
        @Nullable String date,
        double value,
        @Nullable String unit
    ) {}

    @Serdeable
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SleepValue(
        @Nullable String date,
        double value,
        @Nullable String unit,
        @Nullable SleepStages stages
    ) {}

    @Serdeable
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SleepStages(
        double awake,
        double core,
        double deep,
        double rem
    ) {}
}
