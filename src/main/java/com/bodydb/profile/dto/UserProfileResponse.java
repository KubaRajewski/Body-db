package com.bodydb.profile.dto;

import com.bodydb.profile.domain.UserProfile;
import io.micronaut.serde.annotation.Serdeable;
import java.math.BigDecimal;
import java.util.UUID;

@Serdeable
public record UserProfileResponse(
    UUID id,
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
) {
    public static UserProfileResponse from(UserProfile p) {
        return new UserProfileResponse(
            p.getId(), p.getHeightCm(), p.getCurrentWeightKg(), p.getTargetWeightKg(),
            p.getAge(), p.getSex(), p.getGoalDescription(),
            p.getDailyKcalTarget(), p.getProteinTargetG(),
            p.getTrainingDaysPerWeek(), p.getNotes()
        );
    }
}
