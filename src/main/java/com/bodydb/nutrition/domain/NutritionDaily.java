package com.bodydb.nutrition.domain;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@MappedEntity("nutrition_daily")
public class NutritionDaily {
    @Id private UUID id;
    private LocalDate date;
    private Integer caloriesKcal;
    private BigDecimal proteinG;
    private BigDecimal carbsG;
    private BigDecimal fatG;
    private BigDecimal fiberG;
    private BigDecimal sugarG;
    private Integer sodiumMg;
    private Integer waterMl;
    private String source;
    private String notes;
    @DateCreated private Instant createdAt;
    @DateUpdated private Instant updatedAt;

    public NutritionDaily() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate v) { this.date = v; }
    public Integer getCaloriesKcal() { return caloriesKcal; }
    public void setCaloriesKcal(Integer v) { this.caloriesKcal = v; }
    public BigDecimal getProteinG() { return proteinG; }
    public void setProteinG(BigDecimal v) { this.proteinG = v; }
    public BigDecimal getCarbsG() { return carbsG; }
    public void setCarbsG(BigDecimal v) { this.carbsG = v; }
    public BigDecimal getFatG() { return fatG; }
    public void setFatG(BigDecimal v) { this.fatG = v; }
    public BigDecimal getFiberG() { return fiberG; }
    public void setFiberG(BigDecimal v) { this.fiberG = v; }
    public BigDecimal getSugarG() { return sugarG; }
    public void setSugarG(BigDecimal v) { this.sugarG = v; }
    public Integer getSodiumMg() { return sodiumMg; }
    public void setSodiumMg(Integer v) { this.sodiumMg = v; }
    public Integer getWaterMl() { return waterMl; }
    public void setWaterMl(Integer v) { this.waterMl = v; }
    public String getSource() { return source; }
    public void setSource(String v) { this.source = v; }
    public String getNotes() { return notes; }
    public void setNotes(String v) { this.notes = v; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant v) { this.createdAt = v; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant v) { this.updatedAt = v; }
}
