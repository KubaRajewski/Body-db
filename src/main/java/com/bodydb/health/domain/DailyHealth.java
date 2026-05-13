package com.bodydb.health.domain;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@MappedEntity("daily_health")
public class DailyHealth {
    @Id private UUID id;
    private LocalDate date;
    private Integer steps;
    private Integer activeCalories;
    private Integer restingCalories;
    private Integer restingHeartRate;
    private BigDecimal hrvMs;
    private BigDecimal spo2Pct;
    private BigDecimal respiratoryRate;
    // V002 renamed this column: wrist_temperature_delta_c -> wrist_temperature_c
    @io.micronaut.data.annotation.MappedProperty("wrist_temperature_c")
    private BigDecimal wristTemperatureDeltaC;
    private BigDecimal vo2Max;
    private Integer standHours;
    private Integer exerciseMinutes;
    private Integer sleepTotalMin;
    private Integer sleepRemMin;
    private Integer sleepDeepMin;
    private Integer sleepCoreMin;
    private Integer sleepAwakeMin;
    private Integer mindfulMinutes;
    // V002 added columns
    private Integer flightsClimbed;
    @io.micronaut.data.annotation.MappedProperty("walking_distance_m")
    private BigDecimal walkingDistanceM;
    @io.micronaut.data.annotation.MappedProperty("walking_heart_rate_bpm")
    private Integer walkingHeartRateBpm;
    @DateCreated private Instant createdAt;
    @DateUpdated private Instant updatedAt;

    public DailyHealth() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Integer getSteps() { return steps; }
    public void setSteps(Integer steps) { this.steps = steps; }
    public Integer getActiveCalories() { return activeCalories; }
    public void setActiveCalories(Integer v) { this.activeCalories = v; }
    public Integer getRestingCalories() { return restingCalories; }
    public void setRestingCalories(Integer v) { this.restingCalories = v; }
    public Integer getRestingHeartRate() { return restingHeartRate; }
    public void setRestingHeartRate(Integer v) { this.restingHeartRate = v; }
    public BigDecimal getHrvMs() { return hrvMs; }
    public void setHrvMs(BigDecimal v) { this.hrvMs = v; }
    public BigDecimal getSpo2Pct() { return spo2Pct; }
    public void setSpo2Pct(BigDecimal v) { this.spo2Pct = v; }
    public BigDecimal getRespiratoryRate() { return respiratoryRate; }
    public void setRespiratoryRate(BigDecimal v) { this.respiratoryRate = v; }
    public BigDecimal getWristTemperatureDeltaC() { return wristTemperatureDeltaC; }
    public void setWristTemperatureDeltaC(BigDecimal v) { this.wristTemperatureDeltaC = v; }
    public BigDecimal getVo2Max() { return vo2Max; }
    public void setVo2Max(BigDecimal v) { this.vo2Max = v; }
    public Integer getStandHours() { return standHours; }
    public void setStandHours(Integer v) { this.standHours = v; }
    public Integer getExerciseMinutes() { return exerciseMinutes; }
    public void setExerciseMinutes(Integer v) { this.exerciseMinutes = v; }
    public Integer getSleepTotalMin() { return sleepTotalMin; }
    public void setSleepTotalMin(Integer v) { this.sleepTotalMin = v; }
    public Integer getSleepRemMin() { return sleepRemMin; }
    public void setSleepRemMin(Integer v) { this.sleepRemMin = v; }
    public Integer getSleepDeepMin() { return sleepDeepMin; }
    public void setSleepDeepMin(Integer v) { this.sleepDeepMin = v; }
    public Integer getSleepCoreMin() { return sleepCoreMin; }
    public void setSleepCoreMin(Integer v) { this.sleepCoreMin = v; }
    public Integer getSleepAwakeMin() { return sleepAwakeMin; }
    public void setSleepAwakeMin(Integer v) { this.sleepAwakeMin = v; }
    public Integer getMindfulMinutes() { return mindfulMinutes; }
    public void setMindfulMinutes(Integer v) { this.mindfulMinutes = v; }
    public Integer getFlightsClimbed() { return flightsClimbed; }
    public void setFlightsClimbed(Integer v) { this.flightsClimbed = v; }
    public BigDecimal getWalkingDistanceM() { return walkingDistanceM; }
    public void setWalkingDistanceM(BigDecimal v) { this.walkingDistanceM = v; }
    public Integer getWalkingHeartRateBpm() { return walkingHeartRateBpm; }
    public void setWalkingHeartRateBpm(Integer v) { this.walkingHeartRateBpm = v; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant v) { this.createdAt = v; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant v) { this.updatedAt = v; }
}
