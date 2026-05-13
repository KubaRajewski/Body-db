package com.bodydb.health.dto;

import com.bodydb.health.domain.DailyHealth;
import io.micronaut.serde.annotation.Serdeable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Serdeable
public record DailyHealthResponse(
    UUID id,
    LocalDate date,
    Integer steps,
    Integer activeCalories,
    Integer restingCalories,
    Integer restingHeartRate,
    BigDecimal hrvMs,
    BigDecimal spo2Pct,
    BigDecimal respiratoryRate,
    BigDecimal wristTemperatureC,
    BigDecimal vo2Max,
    Integer standHours,
    Integer exerciseMinutes,
    Integer sleepTotalMin,
    Integer sleepRemMin,
    Integer sleepDeepMin,
    Integer sleepCoreMin,
    Integer sleepAwakeMin,
    Integer mindfulMinutes,
    Integer flightsClimbed,
    BigDecimal walkingDistanceM,
    Integer walkingHeartRateBpm
) {

    public static DailyHealthResponse fromEntity(DailyHealth e) {
        return new DailyHealthResponse(
            e.getId(),
            e.getDate(),
            e.getSteps(),
            e.getActiveCalories(),
            e.getRestingCalories(),
            e.getRestingHeartRate(),
            e.getHrvMs(),
            e.getSpo2Pct(),
            e.getRespiratoryRate(),
            e.getWristTemperatureDeltaC(),
            e.getVo2Max(),
            e.getStandHours(),
            e.getExerciseMinutes(),
            e.getSleepTotalMin(),
            e.getSleepRemMin(),
            e.getSleepDeepMin(),
            e.getSleepCoreMin(),
            e.getSleepAwakeMin(),
            e.getMindfulMinutes(),
            e.getFlightsClimbed(),
            e.getWalkingDistanceM(),
            e.getWalkingHeartRateBpm()
        );
    }
}
