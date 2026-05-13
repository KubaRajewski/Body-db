package com.bodydb.profile.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.micronaut.serde.annotation.Serdeable;
import java.math.BigDecimal;

@Serdeable
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserProfileRequest(
    Integer heightCm,
    BigDecimal currentWeightKg,
    BigDecimal targetWeightKg,
    Integer age,
    String sex,
    String goalDescription,
    Integer dailyKcalTarget,
    Integer proteinTargetG,
    Integer trainingDaysPerWeek,
    String notes
) {}
