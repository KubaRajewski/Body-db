package com.bodydb.health.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

@Serdeable
public record DailyHealthRequest(
    @NotNull LocalDate date,
    @PositiveOrZero Integer steps,
    @PositiveOrZero Integer activeCalories,
    @PositiveOrZero Integer restingCalories,
    @PositiveOrZero Integer restingHeartRate,
    @PositiveOrZero BigDecimal hrvMs,
    @PositiveOrZero BigDecimal spo2Pct,
    @PositiveOrZero BigDecimal respiratoryRate,
    @PositiveOrZero BigDecimal wristTemperatureC,
    @PositiveOrZero BigDecimal vo2Max,
    @PositiveOrZero Integer standHours,
    @PositiveOrZero Integer exerciseMinutes,
    @PositiveOrZero Integer sleepTotalMin,
    @PositiveOrZero Integer sleepRemMin,
    @PositiveOrZero Integer sleepDeepMin,
    @PositiveOrZero Integer sleepCoreMin,
    @PositiveOrZero Integer sleepAwakeMin,
    @PositiveOrZero Integer mindfulMinutes,
    @PositiveOrZero Integer flightsClimbed,
    @PositiveOrZero BigDecimal walkingDistanceM,
    @PositiveOrZero Integer walkingHeartRateBpm
) {
}
