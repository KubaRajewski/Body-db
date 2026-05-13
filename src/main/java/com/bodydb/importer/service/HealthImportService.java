package com.bodydb.importer.service;

import com.bodydb.health.domain.DailyHealth;
import com.bodydb.health.repository.DailyHealthRepository;
import com.bodydb.importer.dto.HealthExportDto;
import com.bodydb.importer.dto.HealthExportDto.DailyValue;
import com.bodydb.importer.dto.HealthExportDto.SleepValue;
import com.bodydb.importer.dto.ImportResultDto;
import com.bodydb.nutrition.domain.NutritionDaily;
import com.bodydb.nutrition.repository.NutritionDailyRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Singleton
public class HealthImportService {

    private final DailyHealthRepository healthRepo;
    private final NutritionDailyRepository nutritionRepo;

    public HealthImportService(DailyHealthRepository healthRepo,
                               NutritionDailyRepository nutritionRepo) {
        this.healthRepo = healthRepo;
        this.nutritionRepo = nutritionRepo;
    }

    @Transactional
    public ImportResultDto importHealth(HealthExportDto dto) {
        // Build per-date maps for each metric
        Map<LocalDate, DailyHealth> healthByDate = new HashMap<>();
        Map<LocalDate, NutritionDaily> nutritionByDate = new HashMap<>();

        mapValues(dto.activeCalories(), healthByDate, (h, v) -> h.setActiveCalories((int) v));
        mapValues(dto.basalCalories(),  healthByDate, (h, v) -> h.setRestingCalories((int) v));
        mapValues(dto.stepCount(),      healthByDate, (h, v) -> h.setSteps((int) v));
        mapValues(dto.exerciseMinutes(),healthByDate, (h, v) -> h.setExerciseMinutes((int) v));
        mapValues(dto.restingHeartRate(),healthByDate, (h, v) -> h.setRestingHeartRate((int) v));
        mapValues(dto.heartRateVariability(), healthByDate, (h, v) -> h.setHrvMs(BigDecimal.valueOf(v)));
        mapValues(dto.oxygenSaturation(), healthByDate, (h, v) -> h.setSpo2Pct(BigDecimal.valueOf(v * 100)));
        mapValues(dto.respiratoryRate(), healthByDate, (h, v) -> h.setRespiratoryRate(BigDecimal.valueOf(v)));
        mapValues(dto.wristTemperature(), healthByDate, (h, v) -> h.setWristTemperatureDeltaC(BigDecimal.valueOf(v)));
        mapValues(dto.vo2Max(), healthByDate, (h, v) -> h.setVo2Max(BigDecimal.valueOf(v)));
        mapValues(dto.flightsClimbed(), healthByDate, (h, v) -> h.setFlightsClimbed((int) v));
        mapValues(dto.walkingDistance(), healthByDate, (h, v) -> h.setWalkingDistanceM(BigDecimal.valueOf(v)));
        mapValues(dto.walkingHeartRate(), healthByDate, (h, v) -> h.setWalkingHeartRateBpm((int) v));

        // Sleep — has nested stages
        if (dto.sleepTime() != null) {
            for (SleepValue s : dto.sleepTime()) {
                LocalDate date = LocalDate.parse(s.date());
                DailyHealth h = healthByDate.computeIfAbsent(date, this::newHealth);
                h.setSleepTotalMin((int) s.value());
                if (s.stages() != null) {
                    h.setSleepRemMin((int) s.stages().rem());
                    h.setSleepDeepMin((int) s.stages().deep());
                    h.setSleepCoreMin((int) s.stages().core());
                    h.setSleepAwakeMin((int) s.stages().awake());
                }
            }
        }

        // Nutrition (comes from YAZIO → Apple Health sync)
        mapNutrition(dto.dietaryEnergy(), nutritionByDate, (n, v) -> n.setCaloriesKcal((int) v));
        mapNutrition(dto.dietaryProtein(), nutritionByDate, (n, v) -> n.setProteinG(BigDecimal.valueOf(v)));
        mapNutrition(dto.dietaryCarbohydrates(), nutritionByDate, (n, v) -> n.setCarbsG(BigDecimal.valueOf(v)));
        mapNutrition(dto.dietaryFatTotal(), nutritionByDate, (n, v) -> n.setFatG(BigDecimal.valueOf(v)));

        int upserted = 0;

        for (Map.Entry<LocalDate, DailyHealth> entry : healthByDate.entrySet()) {
            DailyHealth incoming = entry.getValue();
            DailyHealth existing = healthRepo.findByDate(entry.getKey()).orElse(null);
            if (existing == null) {
                healthRepo.save(incoming);
            } else {
                mergeHealth(existing, incoming);
                healthRepo.update(existing);
            }
            upserted++;
        }

        for (Map.Entry<LocalDate, NutritionDaily> entry : nutritionByDate.entrySet()) {
            NutritionDaily incoming = entry.getValue();
            NutritionDaily existing = nutritionRepo.findByDate(entry.getKey()).orElse(null);
            if (existing == null) {
                nutritionRepo.save(incoming);
            } else {
                mergeNutrition(existing, incoming);
                nutritionRepo.update(existing);
            }
        }

        return ImportResultDto.of(upserted, 0);
    }

    // ---- helpers ----

    private interface HealthSetter {
        void set(DailyHealth h, double value);
    }

    private interface NutritionSetter {
        void set(NutritionDaily n, double value);
    }

    private void mapValues(List<DailyValue> list, Map<LocalDate, DailyHealth> map, HealthSetter setter) {
        if (list == null) return;
        for (DailyValue dv : list) {
            LocalDate date = LocalDate.parse(dv.date());
            DailyHealth h = map.computeIfAbsent(date, this::newHealth);
            setter.set(h, dv.value());
        }
    }

    private void mapNutrition(List<DailyValue> list, Map<LocalDate, NutritionDaily> map, NutritionSetter setter) {
        if (list == null) return;
        for (DailyValue dv : list) {
            LocalDate date = LocalDate.parse(dv.date());
            NutritionDaily n = map.computeIfAbsent(date, this::newNutrition);
            setter.set(n, dv.value());
        }
    }

    private DailyHealth newHealth(LocalDate date) {
        DailyHealth h = new DailyHealth();
        h.setId(UUID.randomUUID());
        h.setDate(date);
        return h;
    }

    private NutritionDaily newNutrition(LocalDate date) {
        NutritionDaily n = new NutritionDaily();
        n.setId(UUID.randomUUID());
        n.setDate(date);
        n.setSource("apple_health");
        return n;
    }

    private void mergeHealth(DailyHealth target, DailyHealth src) {
        if (src.getSteps() != null)               target.setSteps(src.getSteps());
        if (src.getActiveCalories() != null)      target.setActiveCalories(src.getActiveCalories());
        if (src.getRestingCalories() != null)     target.setRestingCalories(src.getRestingCalories());
        if (src.getRestingHeartRate() != null)    target.setRestingHeartRate(src.getRestingHeartRate());
        if (src.getHrvMs() != null)               target.setHrvMs(src.getHrvMs());
        if (src.getSpo2Pct() != null)             target.setSpo2Pct(src.getSpo2Pct());
        if (src.getRespiratoryRate() != null)     target.setRespiratoryRate(src.getRespiratoryRate());
        if (src.getWristTemperatureDeltaC() != null) target.setWristTemperatureDeltaC(src.getWristTemperatureDeltaC());
        if (src.getVo2Max() != null)              target.setVo2Max(src.getVo2Max());
        if (src.getExerciseMinutes() != null)     target.setExerciseMinutes(src.getExerciseMinutes());
        if (src.getSleepTotalMin() != null)       target.setSleepTotalMin(src.getSleepTotalMin());
        if (src.getSleepRemMin() != null)         target.setSleepRemMin(src.getSleepRemMin());
        if (src.getSleepDeepMin() != null)        target.setSleepDeepMin(src.getSleepDeepMin());
        if (src.getSleepCoreMin() != null)        target.setSleepCoreMin(src.getSleepCoreMin());
        if (src.getSleepAwakeMin() != null)       target.setSleepAwakeMin(src.getSleepAwakeMin());
    }

    private void mergeNutrition(NutritionDaily target, NutritionDaily src) {
        if (src.getCaloriesKcal() != null) target.setCaloriesKcal(src.getCaloriesKcal());
        if (src.getProteinG() != null)     target.setProteinG(src.getProteinG());
        if (src.getCarbsG() != null)       target.setCarbsG(src.getCarbsG());
        if (src.getFatG() != null)         target.setFatG(src.getFatG());
    }
}
