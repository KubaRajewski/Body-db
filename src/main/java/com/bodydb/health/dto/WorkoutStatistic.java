package com.bodydb.health.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
@JsonIgnoreProperties(ignoreUnknown = true)
public record WorkoutStatistic(
    @Nullable String unit,
    @Nullable Double sum,
    @Nullable Double average,
    @Nullable Double max,
    @Nullable Double min
) {
}
