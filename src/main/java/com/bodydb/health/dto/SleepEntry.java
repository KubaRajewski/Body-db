package com.bodydb.health.dto;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDate;

@Serdeable
public record SleepEntry(LocalDate date, String unit, Double value, @Nullable SleepStages stages) {
}
