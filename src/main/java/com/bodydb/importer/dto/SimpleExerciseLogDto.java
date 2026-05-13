package com.bodydb.importer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.micronaut.serde.annotation.Serdeable;

/**
 * Simple one-exercise-at-a-time payload from the iOS Shortcut.
 * Matches: exercise name, number of sets, rep range string, working weight.
 */
@Serdeable
@JsonIgnoreProperties(ignoreUnknown = true)
public record SimpleExerciseLogDto(
    String date,        // "YYYY-MM-DD" — Current Date formatted
    String exercise,    // e.g. "Back squat"
    int sets,           // e.g. 3
    String repRange,    // e.g. "8-12"
    double weightKg     // e.g. 100.0
) {}
