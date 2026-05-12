package com.bodydb.health.dto;

import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDate;

@Serdeable
public record MetricEntry(LocalDate date, String unit, Double value) {
}
