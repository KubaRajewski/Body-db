package com.bodydb.health.service;

import com.bodydb.health.domain.DailyHealth;
import com.bodydb.health.dto.DailyHealthRequest;
import com.bodydb.health.dto.DailyHealthResponse;
import com.bodydb.health.repository.DailyHealthRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.util.UUID;

@Singleton
public class DailyHealthService {

    private final DailyHealthRepository dailyHealthRepository;

    public DailyHealthService(DailyHealthRepository dailyHealthRepository) {
        this.dailyHealthRepository = dailyHealthRepository;
    }

    @Transactional
    public DailyHealthResponse upsert(DailyHealthRequest request) {
        return dailyHealthRepository.findByDate(request.date())
            .map(existing -> updateExisting(existing, request))
            .orElseGet(() -> createNew(request));
    }

    private DailyHealthResponse createNew(DailyHealthRequest request) {
        DailyHealth dailyHealth = new DailyHealth();
        dailyHealth.setId(UUID.randomUUID());
        applyRequest(dailyHealth, request);
        return DailyHealthResponse.fromEntity(dailyHealthRepository.save(dailyHealth));
    }

    private DailyHealthResponse updateExisting(DailyHealth existing, DailyHealthRequest request) {
        long updated = dailyHealthRepository.updateDailyHealth(
            existing.getId(),
            request.date(),
            request.steps(),
            request.activeCalories(),
            request.restingCalories(),
            request.restingHeartRate(),
            request.hrvMs(),
            request.spo2Pct(),
            request.respiratoryRate(),
            request.wristTemperatureC(),
            request.vo2Max(),
            request.standHours(),
            request.exerciseMinutes(),
            request.sleepTotalMin(),
            request.sleepRemMin(),
            request.sleepDeepMin(),
            request.sleepCoreMin(),
            request.sleepAwakeMin(),
            request.mindfulMinutes(),
            request.flightsClimbed(),
            request.walkingDistanceM(),
            request.walkingHeartRateBpm()
        );

        if (updated != 1) {
            throw new IllegalStateException("Failed to update daily_health for id " + existing.getId());
        }

        return DailyHealthResponse.fromEntity(
            dailyHealthRepository.findById(existing.getId())
                .orElseThrow(() -> new IllegalStateException("Updated row missing for id " + existing.getId()))
        );
    }

    private void applyRequest(DailyHealth h, DailyHealthRequest r) {
        h.setDate(r.date());
        h.setSteps(r.steps());
        h.setActiveCalories(r.activeCalories());
        h.setRestingCalories(r.restingCalories());
        h.setRestingHeartRate(r.restingHeartRate());
        h.setHrvMs(r.hrvMs());
        h.setSpo2Pct(r.spo2Pct());
        h.setRespiratoryRate(r.respiratoryRate());
        h.setWristTemperatureC(r.wristTemperatureC());
        h.setVo2Max(r.vo2Max());
        h.setStandHours(r.standHours());
        h.setExerciseMinutes(r.exerciseMinutes());
        h.setSleepTotalMin(r.sleepTotalMin());
        h.setSleepRemMin(r.sleepRemMin());
        h.setSleepDeepMin(r.sleepDeepMin());
        h.setSleepCoreMin(r.sleepCoreMin());
        h.setSleepAwakeMin(r.sleepAwakeMin());
        h.setMindfulMinutes(r.mindfulMinutes());
        h.setFlightsClimbed(r.flightsClimbed());
        h.setWalkingDistanceM(r.walkingDistanceM());
        h.setWalkingHeartRateBpm(r.walkingHeartRateBpm());
    }
}
