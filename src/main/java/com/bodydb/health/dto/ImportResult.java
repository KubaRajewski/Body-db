package com.bodydb.health.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record ImportResult(int daysUpserted, int workoutsImported) {
}
