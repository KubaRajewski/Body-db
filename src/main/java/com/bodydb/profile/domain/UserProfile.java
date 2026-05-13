package com.bodydb.profile.domain;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@MappedEntity("user_profile")
public class UserProfile {
    @Id private UUID id;
    private Integer heightCm;
    private BigDecimal currentWeightKg;
    private BigDecimal targetWeightKg;
    private Integer age;
    private String sex;
    private String goalDescription;
    private Integer dailyKcalTarget;
    private Integer proteinTargetG;
    private Integer trainingDaysPerWeek;
    private String notes;
    @DateCreated private Instant createdAt;
    @DateUpdated private Instant updatedAt;

    public UserProfile() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Integer getHeightCm() { return heightCm; }
    public void setHeightCm(Integer v) { this.heightCm = v; }
    public BigDecimal getCurrentWeightKg() { return currentWeightKg; }
    public void setCurrentWeightKg(BigDecimal v) { this.currentWeightKg = v; }
    public BigDecimal getTargetWeightKg() { return targetWeightKg; }
    public void setTargetWeightKg(BigDecimal v) { this.targetWeightKg = v; }
    public Integer getAge() { return age; }
    public void setAge(Integer v) { this.age = v; }
    public String getSex() { return sex; }
    public void setSex(String v) { this.sex = v; }
    public String getGoalDescription() { return goalDescription; }
    public void setGoalDescription(String v) { this.goalDescription = v; }
    public Integer getDailyKcalTarget() { return dailyKcalTarget; }
    public void setDailyKcalTarget(Integer v) { this.dailyKcalTarget = v; }
    public Integer getProteinTargetG() { return proteinTargetG; }
    public void setProteinTargetG(Integer v) { this.proteinTargetG = v; }
    public Integer getTrainingDaysPerWeek() { return trainingDaysPerWeek; }
    public void setTrainingDaysPerWeek(Integer v) { this.trainingDaysPerWeek = v; }
    public String getNotes() { return notes; }
    public void setNotes(String v) { this.notes = v; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant v) { this.createdAt = v; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant v) { this.updatedAt = v; }
}
