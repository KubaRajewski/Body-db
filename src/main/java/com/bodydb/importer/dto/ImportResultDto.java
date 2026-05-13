package com.bodydb.importer.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record ImportResultDto(int upserted, int skipped, String message) {
    public static ImportResultDto of(int upserted, int skipped) {
        return new ImportResultDto(upserted, skipped, "OK");
    }
}
