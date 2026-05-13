package com.bodydb.profile.service;

import com.bodydb.profile.domain.UserProfile;
import com.bodydb.profile.dto.UserProfileRequest;
import com.bodydb.profile.repository.UserProfileRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

@Singleton
public class UserProfileService {

    private final UserProfileRepository repo;

    public UserProfileService(UserProfileRepository repo) { this.repo = repo; }

    public Optional<UserProfile> getProfile() {
        return repo.findAll().iterator().hasNext()
            ? Optional.of(repo.findAll().iterator().next())
            : Optional.empty();
    }

    @Transactional
    public UserProfile upsert(UserProfileRequest req) {
        Optional<UserProfile> existing = getProfile();
        UserProfile p = existing.orElseGet(() -> { UserProfile np = new UserProfile(); np.setId(UUID.randomUUID()); return np; });

        if (req.heightCm() != null)               p.setHeightCm(req.heightCm());
        if (req.currentWeightKg() != null)         p.setCurrentWeightKg(req.currentWeightKg());
        if (req.targetWeightKg() != null)          p.setTargetWeightKg(req.targetWeightKg());
        if (req.age() != null)                     p.setAge(req.age());
        if (req.sex() != null)                     p.setSex(req.sex());
        if (req.goalDescription() != null)         p.setGoalDescription(req.goalDescription());
        if (req.dailyKcalTarget() != null)         p.setDailyKcalTarget(req.dailyKcalTarget());
        if (req.proteinTargetG() != null)          p.setProteinTargetG(req.proteinTargetG());
        if (req.trainingDaysPerWeek() != null)     p.setTrainingDaysPerWeek(req.trainingDaysPerWeek());
        if (req.notes() != null)                   p.setNotes(req.notes());

        return existing.isPresent() ? repo.update(p) : repo.save(p);
    }
}
