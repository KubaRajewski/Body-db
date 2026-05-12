package com.bodydb.health.repository;

import com.bodydb.health.domain.Workout;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface WorkoutRepository extends CrudRepository<Workout, UUID> {

    @Query("""
        INSERT INTO workouts (id, date, started_at, type, duration_min, calories, avg_heart_rate, max_heart_rate, distance_m, source, external_id)
        VALUES (:id, :date, :startedAt, :type, :durationMin, :calories, :avgHeartRate, :maxHeartRate, :distanceM, :source, :externalId)
        ON CONFLICT (external_id) WHERE external_id IS NOT NULL
        DO UPDATE SET
            type = EXCLUDED.type,
            duration_min = EXCLUDED.duration_min,
            calories = EXCLUDED.calories,
            avg_heart_rate = EXCLUDED.avg_heart_rate,
            max_heart_rate = EXCLUDED.max_heart_rate,
            distance_m = EXCLUDED.distance_m,
            source = EXCLUDED.source
        """)
    long upsert(
        UUID id,
        LocalDate date,
        Instant startedAt,
        String type,
        Integer durationMin,
        Integer calories,
        Integer avgHeartRate,
        Integer maxHeartRate,
        Integer distanceM,
        String source,
        String externalId
    );
}
