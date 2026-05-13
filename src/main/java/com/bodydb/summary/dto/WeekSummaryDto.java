package com.bodydb.summary.dto;

import com.bodydb.health.domain.DailyHealth;
import com.bodydb.nutrition.domain.NutritionDaily;
import com.bodydb.workout.domain.Workout;
import io.micronaut.serde.annotation.Serdeable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Serdeable
public record WeekSummaryDto(
    LocalDate from,
    LocalDate to,
    List<DaySummary> days,
    Aggregates aggregates
) {

    @Serdeable
    public record DaySummary(
        LocalDate date,
        Integer steps,
        Integer activeCalories,
        Integer restingCalories,
        Integer restingHeartRate,
        BigDecimal hrv,
        Integer sleepTotalMin,
        Integer sleepDeepMin,
        Integer sleepRemMin,
        Integer exerciseMinutes,
        BigDecimal vo2Max,
        Integer caloriesEaten,
        BigDecimal proteinG,
        BigDecimal carbsG,
        BigDecimal fatG,
        List<WorkoutSummary> workouts
    ) {}

    @Serdeable
    public record WorkoutSummary(
        String type,
        Integer durationMin,
        Integer calories,
        Integer avgHr
    ) {}

    @Serdeable
    public record Aggregates(
        long totalSteps,
        long totalActiveCalories,
        int totalWorkouts,
        int totalWorkoutMinutes,
        Double avgSleepMin,
        Double avgRestingHr,
        Double avgHrv,
        Double avgCaloriesEaten,
        Double avgProteinG
    ) {}
}
