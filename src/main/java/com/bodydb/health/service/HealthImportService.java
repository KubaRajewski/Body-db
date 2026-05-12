package com.bodydb.health.service;

import com.bodydb.health.dto.DailyHealthRequest;
import com.bodydb.health.dto.HealthExportRequest;
import com.bodydb.health.dto.MetricEntry;
import com.bodydb.health.dto.SleepEntry;
import com.bodydb.health.dto.WorkoutEntry;
import com.bodydb.health.dto.WorkoutStatistic;
import com.bodydb.health.dto.WorkoutsExportRequest;
import com.bodydb.health.repository.WorkoutRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

@Singleton
public class HealthImportService {

    private final DailyHealthService dailyHealthService;
    private final WorkoutRepository workoutRepository;

    public HealthImportService(DailyHealthService dailyHealthService, WorkoutRepository workoutRepository) {
        this.dailyHealthService = dailyHealthService;
        this.workoutRepository = workoutRepository;
    }

    @Transactional
    public int importHealth(HealthExportRequest req) {
        Map<LocalDate, DailyData> dayMap = new LinkedHashMap<>();

        addMetric(dayMap, req.stepCount(), (d, v) -> d.steps = v.intValue());
        addMetric(dayMap, req.activeCalories(), (d, v) -> d.activeCalories = v.intValue());
        addMetric(dayMap, req.basalCalories(), (d, v) -> d.restingCalories = v.intValue());
        addMetric(dayMap, req.restingHeartRate(), (d, v) -> d.restingHeartRate = v.intValue());
        addMetric(dayMap, req.heartRateVariability(), (d, v) -> d.hrvMs = BigDecimal.valueOf(v));
        addMetric(dayMap, req.oxygenSaturation(), (d, v) -> d.spo2Pct = BigDecimal.valueOf(v * 100.0));
        addMetric(dayMap, req.respiratoryRate(), (d, v) -> d.respiratoryRate = BigDecimal.valueOf(v));
        addMetric(dayMap, req.wristTemperature(), (d, v) -> d.wristTemperatureC = BigDecimal.valueOf(v));
        addMetric(dayMap, req.vo2Max(), (d, v) -> d.vo2Max = BigDecimal.valueOf(v));
        addMetric(dayMap, req.exerciseMinutes(), (d, v) -> d.exerciseMinutes = v.intValue());
        addMetric(dayMap, req.flightsClimbed(), (d, v) -> d.flightsClimbed = v.intValue());
        addMetric(dayMap, req.walkingDistance(), (d, v) -> d.walkingDistanceM = BigDecimal.valueOf(v));
        addMetric(dayMap, req.walkingHeartRate(), (d, v) -> d.walkingHeartRateBpm = v.intValue());

        if (req.sleepTime() != null) {
            for (SleepEntry entry : req.sleepTime()) {
                DailyData d = dayMap.computeIfAbsent(entry.date(), k -> new DailyData());
                if (entry.value() != null) d.sleepTotalMin = entry.value().intValue();
                if (entry.stages() != null) {
                    if (entry.stages().rem() != null) d.sleepRemMin = entry.stages().rem().intValue();
                    if (entry.stages().deep() != null) d.sleepDeepMin = entry.stages().deep().intValue();
                    if (entry.stages().core() != null) d.sleepCoreMin = entry.stages().core().intValue();
                    if (entry.stages().awake() != null) d.sleepAwakeMin = entry.stages().awake().intValue();
                }
            }
        }

        for (Map.Entry<LocalDate, DailyData> entry : dayMap.entrySet()) {
            dailyHealthService.upsert(entry.getValue().toRequest(entry.getKey()));
        }
        return dayMap.size();
    }

    @Transactional
    public int importWorkouts(WorkoutsExportRequest req) {
        if (req.workouts() == null) return 0;
        for (WorkoutEntry entry : req.workouts()) {
            LocalDate date = entry.startDate().atZone(ZoneOffset.UTC).toLocalDate();
            int durationMin = (int) Math.round(entry.duration() / 60.0);

            Integer calories = null;
            Integer avgHr = null;
            Integer maxHr = null;
            Integer distanceM = null;

            if (entry.statistics() != null) {
                WorkoutStatistic energy = entry.statistics().get("HKQuantityTypeIdentifierActiveEnergyBurned");
                if (energy != null && energy.sum() != null) calories = energy.sum().intValue();

                WorkoutStatistic hr = entry.statistics().get("HKQuantityTypeIdentifierHeartRate");
                if (hr != null) {
                    if (hr.average() != null) avgHr = hr.average().intValue();
                    if (hr.max() != null) maxHr = hr.max().intValue();
                }

                WorkoutStatistic dist = entry.statistics().get("HKQuantityTypeIdentifierDistanceWalkingRunning");
                if (dist == null) dist = entry.statistics().get("HKQuantityTypeIdentifierDistanceCycling");
                if (dist != null && dist.sum() != null) distanceM = dist.sum().intValue();
            }

            workoutRepository.upsert(
                UUID.randomUUID(), date, entry.startDate(), entry.activityType(),
                durationMin, calories, avgHr, maxHr, distanceM,
                entry.source(), entry.startDate().toString()
            );
        }
        return req.workouts().size();
    }

    private void addMetric(Map<LocalDate, DailyData> map, List<MetricEntry> entries,
                           BiConsumer<DailyData, Double> setter) {
        if (entries == null) return;
        for (MetricEntry entry : entries) {
            if (entry.value() != null) {
                setter.accept(map.computeIfAbsent(entry.date(), k -> new DailyData()), entry.value());
            }
        }
    }

    private static class DailyData {
        Integer steps, activeCalories, restingCalories, restingHeartRate;
        BigDecimal hrvMs, spo2Pct, respiratoryRate, wristTemperatureC, vo2Max, walkingDistanceM;
        Integer exerciseMinutes, flightsClimbed, walkingHeartRateBpm;
        Integer sleepTotalMin, sleepRemMin, sleepDeepMin, sleepCoreMin, sleepAwakeMin;

        DailyHealthRequest toRequest(LocalDate date) {
            return new DailyHealthRequest(
                date, steps, activeCalories, restingCalories, restingHeartRate,
                hrvMs, spo2Pct, respiratoryRate, wristTemperatureC, vo2Max,
                null, exerciseMinutes,
                sleepTotalMin, sleepRemMin, sleepDeepMin, sleepCoreMin, sleepAwakeMin,
                null, flightsClimbed, walkingDistanceM, walkingHeartRateBpm
            );
        }
    }
}
