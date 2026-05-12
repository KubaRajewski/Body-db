package com.bodydb.health.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record SleepStages(Double awake, Double core, Double deep, Double rem, Double unspecified) {
}
