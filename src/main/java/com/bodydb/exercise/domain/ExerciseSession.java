package com.bodydb.exercise.domain;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@MappedEntity("exercise_sessions")
public class ExerciseSession {
    @Id private UUID id;
    private LocalDate date;
    private Instant startedAt;
    private Instant endedAt;
    private String title;
    private String notes;
    @DateCreated private Instant createdAt;
    @DateUpdated private Instant updatedAt;

    public ExerciseSession() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate v) { this.date = v; }
    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant v) { this.startedAt = v; }
    public Instant getEndedAt() { return endedAt; }
    public void setEndedAt(Instant v) { this.endedAt = v; }
    public String getTitle() { return title; }
    public void setTitle(String v) { this.title = v; }
    public String getNotes() { return notes; }
    public void setNotes(String v) { this.notes = v; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant v) { this.createdAt = v; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant v) { this.updatedAt = v; }
}
