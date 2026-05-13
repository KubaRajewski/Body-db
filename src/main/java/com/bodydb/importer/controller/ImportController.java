package com.bodydb.importer.controller;

import com.bodydb.importer.dto.ExerciseImportDto;
import com.bodydb.importer.dto.HealthExportDto;
import com.bodydb.importer.dto.ImportResultDto;
import com.bodydb.importer.dto.SimpleExerciseLogDto;
import com.bodydb.importer.dto.WorkoutExportDto;
import com.bodydb.importer.service.ExerciseImportService;
import com.bodydb.importer.service.HealthImportService;
import com.bodydb.importer.service.WorkoutImportService;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;

import jakarta.validation.Valid;

@Controller("/import")
public class ImportController {

    private final HealthImportService healthService;
    private final WorkoutImportService workoutService;
    private final ExerciseImportService exerciseService;

    public ImportController(HealthImportService healthService,
                            WorkoutImportService workoutService,
                            ExerciseImportService exerciseService) {
        this.healthService = healthService;
        this.workoutService = workoutService;
        this.exerciseService = exerciseService;
    }

    /** Accepts the native Apple Health export JSON (multi-day batch). */
    @Post("/health")
    public ImportResultDto importHealth(@Body HealthExportDto dto) {
        return healthService.importHealth(dto);
    }

    /** Accepts the native Apple Workouts export JSON (array of workouts). */
    @Post("/workout")
    public ImportResultDto importWorkout(@Body WorkoutExportDto dto) {
        return workoutService.importWorkouts(dto);
    }

    /** Accepts a manual exercise session from the iOS Shortcut menu. */
    @Post("/exercise")
    public ImportResultDto importExercise(@Body ExerciseImportDto dto) {
        return exerciseService.importExercise(dto);
    }

    /** Simple one-exercise log from Shortcut: exercise + sets count + rep range + weight. */
    @Post("/exercise/log")
    public ImportResultDto logExercise(@Body SimpleExerciseLogDto dto) {
        return exerciseService.logExercise(dto);
    }
}
