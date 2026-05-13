package com.bodydb.importer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;
import java.util.Map;

/**
 * Maps the native Apple Health export JSON from the iOS Shortcut.
 * Arrays are per-day aggregates; sleepTime has nested stages.
 */
@Serdeable
@JsonIgnoreProperties(ignoreUnknown = true)
public record HealthExportDto(
    List<DailyValue> activeCalories,
    List<DailyValue> basalCalories,
    List<DailyValue> stepCount,
    List<DailyValue> walkingDistance,
    List<DailyValue> exerciseMinutes,
    List<DailyValue> restingHeartRate,
    List<DailyValue> heartRateVariability,
    List<DailyValue> oxygenSaturation,
    List<DailyValue> respiratoryRate,
    List<DailyValue> wristTemperature,
    List<DailyValue> vo2Max,
    List<DailyValue> flightsClimbed,
    List<DailyValue> walkingHeartRate,
    List<SleepValue> sleepTime,
    List<DailyValue> dietaryEnergy,
    List<DailyValue> dietaryProtein,
    List<DailyValue> dietaryCarbohydrates,
    List<DailyValue> dietaryFatTotal,
    List<DailyValue> weight
) {

    @Serdeable
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DailyValue(String date, double value, String unit) {}

    @Serdeable
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SleepValue(String date, double value, String unit, SleepStages stages) {}

    @Serdeable
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SleepStages(double awake, double core, double deep, double rem) {}
}
