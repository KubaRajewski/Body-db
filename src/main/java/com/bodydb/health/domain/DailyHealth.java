package com.bodydb.health.domain;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@MappedEntity("daily_health")
public class DailyHealth {

    @Id
    private UUID id;

    private LocalDate date;

    private Integer steps;

    @MappedProperty("active_calories")
    private Integer activeCalories;

    @MappedProperty("resting_calories")
    private Integer restingCalories;

    @MappedProperty("resting_heart_rate")
    private Integer restingHeartRate;

    @MappedProperty("hrv_ms")
    private BigDecimal hrvMs;

    @MappedProperty("spo2_pct")
    private BigDecimal spo2Pct;

    @MappedProperty("respiratory_rate")
    private BigDecimal respiratoryRate;

    @MappedProperty("wrist_temperature_c")
    private BigDecimal wristTemperatureC;

    @MappedProperty("vo2_max")
    private BigDecimal vo2Max;

    @MappedProperty("stand_hours")
    private Integer standHours;

    @MappedProperty("exercise_minutes")
    private Integer exerciseMinutes;

    @MappedProperty("sleep_total_min")
    private Integer sleepTotalMin;

    @MappedProperty("sleep_rem_min")
    private Integer sleepRemMin;

    @MappedProperty("sleep_deep_min")
    private Integer sleepDeepMin;

    @MappedProperty("sleep_core_min")
    private Integer sleepCoreMin;

    @MappedProperty("sleep_awake_min")
    private Integer sleepAwakeMin;

    @MappedProperty("mindful_minutes")
    private Integer mindfulMinutes;

    @MappedProperty("flights_climbed")
    private Integer flightsClimbed;

    @MappedProperty("walking_distance_m")
    private BigDecimal walkingDistanceM;

    @MappedProperty("walking_heart_rate_bpm")
    private Integer walkingHeartRateBpm;

    @DateCreated
    @MappedProperty("created_at")
    private Instant createdAt;

    @DateUpdated
    @MappedProperty("updated_at")
    private Instant updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Integer getSteps() { return steps; }
    public void setSteps(Integer steps) { this.steps = steps; }

    public Integer getActiveCalories() { return activeCalories; }
    public void setActiveCalories(Integer activeCalories) { this.activeCalories = activeCalories; }

    public Integer getRestingCalories() { return restingCalories; }
    public void setRestingCalories(Integer restingCalories) { this.restingCalories = restingCalories; }

    public Integer getRestingHeartRate() { return restingHeartRate; }
    public void setRestingHeartRate(Integer restingHeartRate) { this.restingHeartRate = restingHeartRate; }

    public BigDecimal getHrvMs() { return hrvMs; }
    public void setHrvMs(BigDecimal hrvMs) { this.hrvMs = hrvMs; }

    public BigDecimal getSpo2Pct() { return spo2Pct; }
    public void setSpo2Pct(BigDecimal spo2Pct) { this.spo2Pct = spo2Pct; }

    public BigDecimal getRespiratoryRate() { return respiratoryRate; }
    public void setRespiratoryRate(BigDecimal respiratoryRate) { this.respiratoryRate = respiratoryRate; }

    public BigDecimal getWristTemperatureC() { return wristTemperatureC; }
    public void setWristTemperatureC(BigDecimal wristTemperatureC) { this.wristTemperatureC = wristTemperatureC; }

    public BigDecimal getVo2Max() { return vo2Max; }
    public void setVo2Max(BigDecimal vo2Max) { this.vo2Max = vo2Max; }

    public Integer getStandHours() { return standHours; }
    public void setStandHours(Integer standHours) { this.standHours = standHours; }

    public Integer getExerciseMinutes() { return exerciseMinutes; }
    public void setExerciseMinutes(Integer exerciseMinutes) { this.exerciseMinutes = exerciseMinutes; }

    public Integer getSleepTotalMin() { return sleepTotalMin; }
    public void setSleepTotalMin(Integer sleepTotalMin) { this.sleepTotalMin = sleepTotalMin; }

    public Integer getSleepRemMin() { return sleepRemMin; }
    public void setSleepRemMin(Integer sleepRemMin) { this.sleepRemMin = sleepRemMin; }

    public Integer getSleepDeepMin() { return sleepDeepMin; }
    public void setSleepDeepMin(Integer sleepDeepMin) { this.sleepDeepMin = sleepDeepMin; }

    public Integer getSleepCoreMin() { return sleepCoreMin; }
    public void setSleepCoreMin(Integer sleepCoreMin) { this.sleepCoreMin = sleepCoreMin; }

    public Integer getSleepAwakeMin() { return sleepAwakeMin; }
    public void setSleepAwakeMin(Integer sleepAwakeMin) { this.sleepAwakeMin = sleepAwakeMin; }

    public Integer getMindfulMinutes() { return mindfulMinutes; }
    public void setMindfulMinutes(Integer mindfulMinutes) { this.mindfulMinutes = mindfulMinutes; }

    public Integer getFlightsClimbed() { return flightsClimbed; }
    public void setFlightsClimbed(Integer flightsClimbed) { this.flightsClimbed = flightsClimbed; }

    public BigDecimal getWalkingDistanceM() { return walkingDistanceM; }
    public void setWalkingDistanceM(BigDecimal walkingDistanceM) { this.walkingDistanceM = walkingDistanceM; }

    public Integer getWalkingHeartRateBpm() { return walkingHeartRateBpm; }
    public void setWalkingHeartRateBpm(Integer walkingHeartRateBpm) { this.walkingHeartRateBpm = walkingHeartRateBpm; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
