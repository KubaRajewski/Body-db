package com.bodydb.summary.service;

import com.bodydb.health.domain.DailyHealth;
import com.bodydb.health.repository.DailyHealthRepository;
import com.bodydb.nutrition.domain.NutritionDaily;
import com.bodydb.nutrition.repository.NutritionDailyRepository;
import com.bodydb.summary.dto.WeekSummaryDto;
import com.bodydb.workout.domain.Workout;
import com.bodydb.workout.repository.WorkoutRepository;
import jakarta.inject.Singleton;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public class SummaryService {

    private final DailyHealthRepository healthRepo;
    private final WorkoutRepository workoutRepo;
    private final NutritionDailyRepository nutritionRepo;

    public SummaryService(DailyHealthRepository healthRepo,
                          WorkoutRepository workoutRepo,
                          NutritionDailyRepository nutritionRepo) {
        this.healthRepo = healthRepo;
        this.workoutRepo = workoutRepo;
        this.nutritionRepo = nutritionRepo;
    }

    public WeekSummaryDto week(LocalDate from) {
        LocalDate to = from.plusDays(6);

        List<DailyHealth> healthList = healthRepo.findByDateBetween(from, to);
        List<Workout> workoutList = workoutRepo.findByDateBetween(from, to);
        List<NutritionDaily> nutritionList = nutritionRepo.findByDateBetween(from, to);

        Map<LocalDate, DailyHealth> healthMap = healthList.stream()
            .collect(Collectors.toMap(DailyHealth::getDate, h -> h));
        Map<LocalDate, NutritionDaily> nutritionMap = nutritionList.stream()
            .collect(Collectors.toMap(NutritionDaily::getDate, n -> n));
        Map<LocalDate, List<Workout>> workoutMap = workoutList.stream()
            .collect(Collectors.groupingBy(Workout::getDate));

        List<WeekSummaryDto.DaySummary> days = new ArrayList<>();
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            DailyHealth h = healthMap.get(d);
            NutritionDaily n = nutritionMap.get(d);
            List<Workout> ws = workoutMap.getOrDefault(d, List.of());
            days.add(toDay(d, h, n, ws));
        }

        return new WeekSummaryDto(from, to, days, aggregates(days));
    }

    private WeekSummaryDto.DaySummary toDay(LocalDate date, DailyHealth h, NutritionDaily n, List<Workout> ws) {
        List<WeekSummaryDto.WorkoutSummary> workoutSummaries = ws.stream()
            .map(w -> new WeekSummaryDto.WorkoutSummary(w.getType(), w.getDurationMin(), w.getCalories(), w.getAvgHeartRate()))
            .toList();

        return new WeekSummaryDto.DaySummary(
            date,
            h != null ? h.getSteps() : null,
            h != null ? h.getActiveCalories() : null,
            h != null ? h.getRestingCalories() : null,
            h != null ? h.getRestingHeartRate() : null,
            h != null ? h.getHrvMs() : null,
            h != null ? h.getSleepTotalMin() : null,
            h != null ? h.getSleepDeepMin() : null,
            h != null ? h.getSleepRemMin() : null,
            h != null ? h.getExerciseMinutes() : null,
            h != null ? h.getVo2Max() : null,
            n != null ? n.getCaloriesKcal() : null,
            n != null ? n.getProteinG() : null,
            n != null ? n.getCarbsG() : null,
            n != null ? n.getFatG() : null,
            workoutSummaries
        );
    }

    private WeekSummaryDto.Aggregates aggregates(List<WeekSummaryDto.DaySummary> days) {
        long totalSteps = days.stream().filter(d -> d.steps() != null).mapToLong(d -> d.steps()).sum();
        long totalActive = days.stream().filter(d -> d.activeCalories() != null).mapToLong(d -> d.activeCalories()).sum();
        int totalWorkouts = days.stream().mapToInt(d -> d.workouts().size()).sum();
        int totalWorkoutMin = days.stream().flatMap(d -> d.workouts().stream())
            .filter(w -> w.durationMin() != null).mapToInt(w -> w.durationMin()).sum();
        Double avgSleep = avg(days.stream().filter(d -> d.sleepTotalMin() != null).mapToDouble(d -> d.sleepTotalMin()).toArray());
        Double avgRhr = avg(days.stream().filter(d -> d.restingHeartRate() != null).mapToDouble(d -> d.restingHeartRate()).toArray());
        Double avgHrv = avg(days.stream().filter(d -> d.hrv() != null).mapToDouble(d -> d.hrv().doubleValue()).toArray());
        Double avgKcal = avg(days.stream().filter(d -> d.caloriesEaten() != null).mapToDouble(d -> d.caloriesEaten()).toArray());
        Double avgProtein = avg(days.stream().filter(d -> d.proteinG() != null).mapToDouble(d -> d.proteinG().doubleValue()).toArray());

        return new WeekSummaryDto.Aggregates(totalSteps, totalActive, totalWorkouts, totalWorkoutMin, avgSleep, avgRhr, avgHrv, avgKcal, avgProtein);
    }

    private Double avg(double[] arr) {
        if (arr.length == 0) return null;
        double sum = 0; for (double v : arr) sum += v;
        return Math.round(sum / arr.length * 10.0) / 10.0;
    }
}
