package com.bodydb.brain.service;

import com.bodydb.health.domain.DailyHealth;
import com.bodydb.health.repository.DailyHealthRepository;
import com.bodydb.nutrition.domain.NutritionDaily;
import com.bodydb.nutrition.repository.NutritionDailyRepository;
import com.bodydb.profile.domain.UserProfile;
import com.bodydb.profile.service.UserProfileService;
import com.bodydb.summary.dto.WeekSummaryDto;
import com.bodydb.summary.service.SummaryService;
import com.bodydb.workout.domain.Workout;
import com.bodydb.workout.repository.WorkoutRepository;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Singleton
public class BrainService {

    private static final Logger log = LoggerFactory.getLogger(BrainService.class);

    private final SummaryService summaryService;
    private final UserProfileService profileService;
    private final AnthropicClient anthropic;
    private final TelegramService telegram;

    public BrainService(SummaryService summaryService,
                        UserProfileService profileService,
                        AnthropicClient anthropic,
                        TelegramService telegram) {
        this.summaryService = summaryService;
        this.profileService = profileService;
        this.anthropic = anthropic;
        this.telegram = telegram;
    }

    /** Called by the scheduler every morning. */
    public void generateAndSendMorningBriefing() {
        log.info("Generating morning briefing for {}", LocalDate.now());

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6);
        WeekSummaryDto week = summaryService.week(weekStart);
        Optional<UserProfile> profile = profileService.getProfile();

        String systemPrompt = buildSystemPrompt(profile.orElse(null));
        String userMessage = buildUserMessage(week, today);

        String briefing = anthropic.complete(systemPrompt, userMessage);
        telegram.sendMessage(briefing);

        log.info("Morning briefing sent.");
    }

    private String buildSystemPrompt(UserProfile profile) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a personal fitness coach and nutrition advisor. ");
        sb.append("You have full access to the user's biometric and training data. ");
        sb.append("Be concise, specific and motivating. Use Markdown formatting.\n\n");

        if (profile != null) {
            sb.append("## User Profile\n");
            if (profile.getHeightCm() != null)           sb.append("- Height: ").append(profile.getHeightCm()).append(" cm\n");
            if (profile.getCurrentWeightKg() != null)    sb.append("- Current weight: ").append(profile.getCurrentWeightKg()).append(" kg\n");
            if (profile.getTargetWeightKg() != null)     sb.append("- Target weight: ").append(profile.getTargetWeightKg()).append(" kg\n");
            if (profile.getAge() != null)                sb.append("- Age: ").append(profile.getAge()).append("\n");
            if (profile.getSex() != null)                sb.append("- Sex: ").append(profile.getSex()).append("\n");
            if (profile.getDailyKcalTarget() != null)    sb.append("- Daily kcal target: ").append(profile.getDailyKcalTarget()).append(" kcal\n");
            if (profile.getProteinTargetG() != null)     sb.append("- Daily protein target: ").append(profile.getProteinTargetG()).append(" g\n");
            if (profile.getTrainingDaysPerWeek() != null) sb.append("- Training days/week: ").append(profile.getTrainingDaysPerWeek()).append("\n");
            if (profile.getGoalDescription() != null)    sb.append("- Goal: ").append(profile.getGoalDescription()).append("\n");
            if (profile.getNotes() != null)              sb.append("- Strategy notes: ").append(profile.getNotes()).append("\n");
        }
        return sb.toString();
    }

    private String buildUserMessage(WeekSummaryDto week, LocalDate today) {
        StringBuilder sb = new StringBuilder();
        sb.append("Today is ").append(today.format(DateTimeFormatter.ISO_LOCAL_DATE)).append(".\n\n");
        sb.append("## Last 7 Days Summary\n\n");

        for (WeekSummaryDto.DaySummary day : week.days()) {
            sb.append("### ").append(day.date()).append("\n");
            if (day.steps() != null)           sb.append("- Steps: ").append(day.steps()).append("\n");
            if (day.activeCalories() != null)  sb.append("- Active calories: ").append(day.activeCalories()).append(" kcal\n");
            if (day.restingCalories() != null) sb.append("- Resting calories: ").append(day.restingCalories()).append(" kcal\n");
            if (day.caloriesEaten() != null)   sb.append("- 🍽️ Food intake: ").append(day.caloriesEaten()).append(" kcal");
            if (day.proteinG() != null)        sb.append(" | protein: ").append(day.proteinG()).append("g");
            if (day.carbsG() != null)          sb.append(" | carbs: ").append(day.carbsG()).append("g");
            if (day.fatG() != null)            sb.append(" | fat: ").append(day.fatG()).append("g");
            if (day.caloriesEaten() != null)   sb.append("\n");
            if (day.sleepTotalMin() != null)   sb.append("- 😴 Sleep: ").append(day.sleepTotalMin()).append(" min total");
            if (day.sleepDeepMin() != null)    sb.append(" (deep: ").append(day.sleepDeepMin()).append("min)");
            if (day.sleepTotalMin() != null)   sb.append("\n");
            if (day.restingHeartRate() != null) sb.append("- RHR: ").append(day.restingHeartRate()).append(" bpm");
            if (day.hrv() != null)             sb.append(" | HRV: ").append(day.hrv()).append(" ms");
            if (day.restingHeartRate() != null) sb.append("\n");
            if (!day.workouts().isEmpty()) {
                sb.append("- Workouts:\n");
                for (var w : day.workouts()) {
                    sb.append("  - ").append(w.type()).append(" ").append(w.durationMin()).append("min");
                    if (w.calories() != null) sb.append(", ").append(w.calories()).append(" kcal");
                    if (w.avgHr() != null)    sb.append(", avg HR ").append(w.avgHr()).append(" bpm");
                    sb.append("\n");
                }
            }
            sb.append("\n");
        }

        if (week.aggregates() != null) {
            var agg = week.aggregates();
            sb.append("## Weekly Aggregates\n");
            sb.append("- Total steps: ").append(agg.totalSteps()).append("\n");
            sb.append("- Total active kcal: ").append(agg.totalActiveCalories()).append("\n");
            sb.append("- Total workouts: ").append(agg.totalWorkouts()).append(" (").append(agg.totalWorkoutMinutes()).append(" min)\n");
            if (agg.avgSleepMin() != null) sb.append("- Avg sleep: ").append(agg.avgSleepMin()).append(" min\n");
            if (agg.avgRestingHr() != null) sb.append("- Avg RHR: ").append(agg.avgRestingHr()).append(" bpm\n");
            if (agg.avgHrv() != null) sb.append("- Avg HRV: ").append(agg.avgHrv()).append(" ms\n");
            if (agg.avgCaloriesEaten() != null) sb.append("- Avg daily kcal eaten: ").append(agg.avgCaloriesEaten()).append("\n");
            if (agg.avgProteinG() != null) sb.append("- Avg daily protein: ").append(agg.avgProteinG()).append(" g\n");
        }

        sb.append("\n---\n");
        sb.append("Based on the above data and the long-term strategy in your system prompt, ");
        sb.append("provide a morning briefing with:\n");
        sb.append("1. **Yesterday's recap** — key highlights and any concerns\n");
        sb.append("2. **Today's calorie target** — adjusted based on recent trend and goal\n");
        sb.append("3. **Today's workout plan** — specific session recommendation with sets/reps if applicable\n");
        sb.append("4. **One key focus** — the single most impactful habit to focus on today\n");

        return sb.toString();
    }
}
