package com.bodydb.exercise.domain;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.DateCreated;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@MappedEntity("exercise_sets")
public class ExerciseSet {
    @Id private UUID id;
    private UUID sessionId;
    private Integer setOrder;
    private String exerciseName;
    private Integer reps;
    private BigDecimal weightKg;
    private Integer durationSec;
    private Integer distanceM;
    private BigDecimal rpe;
    private String notes;
    @DateCreated private Instant createdAt;

    public ExerciseSet() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSessionId() { return sessionId; }
    public void setSessionId(UUID v) { this.sessionId = v; }
    public Integer getSetOrder() { return setOrder; }
    public void setSetOrder(Integer v) { this.setOrder = v; }
    public String getExerciseName() { return exerciseName; }
    public void setExerciseName(String v) { this.exerciseName = v; }
    public Integer getReps() { return reps; }
    public void setReps(Integer v) { this.reps = v; }
    public BigDecimal getWeightKg() { return weightKg; }
    public void setWeightKg(BigDecimal v) { this.weightKg = v; }
    public Integer getDurationSec() { return durationSec; }
    public void setDurationSec(Integer v) { this.durationSec = v; }
    public Integer getDistanceM() { return distanceM; }
    public void setDistanceM(Integer v) { this.distanceM = v; }
    public BigDecimal getRpe() { return rpe; }
    public void setRpe(BigDecimal v) { this.rpe = v; }
    public String getNotes() { return notes; }
    public void setNotes(String v) { this.notes = v; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant v) { this.createdAt = v; }
}
