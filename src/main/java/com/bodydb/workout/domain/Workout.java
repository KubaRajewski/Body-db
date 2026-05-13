package com.bodydb.workout.domain;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.DateCreated;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@MappedEntity("workouts")
public class Workout {
    @Id private UUID id;
    private LocalDate date;
    private Instant startedAt;
    private String type;
    private Integer durationMin;
    private Integer calories;
    private Integer avgHeartRate;
    private Integer maxHeartRate;
    private Integer distanceM;
    private String source;
    private String externalId;
    @DateCreated private Instant createdAt;

    public Workout() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate v) { this.date = v; }
    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant v) { this.startedAt = v; }
    public String getType() { return type; }
    public void setType(String v) { this.type = v; }
    public Integer getDurationMin() { return durationMin; }
    public void setDurationMin(Integer v) { this.durationMin = v; }
    public Integer getCalories() { return calories; }
    public void setCalories(Integer v) { this.calories = v; }
    public Integer getAvgHeartRate() { return avgHeartRate; }
    public void setAvgHeartRate(Integer v) { this.avgHeartRate = v; }
    public Integer getMaxHeartRate() { return maxHeartRate; }
    public void setMaxHeartRate(Integer v) { this.maxHeartRate = v; }
    public Integer getDistanceM() { return distanceM; }
    public void setDistanceM(Integer v) { this.distanceM = v; }
    public String getSource() { return source; }
    public void setSource(String v) { this.source = v; }
    public String getExternalId() { return externalId; }
    public void setExternalId(String v) { this.externalId = v; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant v) { this.createdAt = v; }
}
