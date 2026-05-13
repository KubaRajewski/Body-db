package com.bodydb.importer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

/**
 * Payload from the iOS Shortcut exercise-logging menu.
 * The Shortcut collects sets for a session and POSTs them together.
 */
@Serdeable
@JsonIgnoreProperties(ignoreUnknown = true)
public record ExerciseImportDto(
    String date,         // "YYYY-MM-DD"
    String startedAt,    // ISO-8601 optional
    String title,        // session title e.g. "Push day"
    String notes,
    List<SetEntry> sets
) {

    @Serdeable
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SetEntry(
        int setOrder,
        String exercise,
        Integer reps,
        Double weightKg,
        Integer durationSec,
        Double rpe,
        String notes
    ) {}
}
