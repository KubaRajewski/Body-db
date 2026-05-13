package com.bodydb.health.domain;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedProperty;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * @deprecated Use com.bodydb.workout.domain.Workout instead.
 */
@Deprecated
public class Workout {

    @Id
    private UUID id;

    private LocalDate date;

    @MappedProperty("started_at")
    private Instant startedAt;

    @MappedProperty("type")
    private String type;

    @MappedProperty("duration_min")
    private Integer durationMin;

    private Integer calories;

    @MappedProperty("avg_heart_rate")
    private Integer avgHeartRate;

    @MappedProperty("max_heart_rate")
    private Integer maxHeartRate;

    @MappedProperty("distance_m")
    private Integer distanceM;

    private String source;

    @MappedProperty("external_id")
    private String externalId;

    @DateCreated
    @MappedProperty("created_at")
    private Instant createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getDurationMin() { return durationMin; }
    public void setDurationMin(Integer durationMin) { this.durationMin = durationMin; }

    public Integer getCalories() { return calories; }
    public void setCalories(Integer calories) { this.calories = calories; }

    public Integer getAvgHeartRate() { return avgHeartRate; }
    public void setAvgHeartRate(Integer avgHeartRate) { this.avgHeartRate = avgHeartRate; }

    public Integer getMaxHeartRate() { return maxHeartRate; }
    public void setMaxHeartRate(Integer maxHeartRate) { this.maxHeartRate = maxHeartRate; }

    public Integer getDistanceM() { return distanceM; }
    public void setDistanceM(Integer distanceM) { this.distanceM = distanceM; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
