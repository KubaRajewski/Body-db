package com.bodydb.health.repository;

import com.bodydb.health.domain.DailyHealth;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface DailyHealthRepository extends CrudRepository<DailyHealth, UUID> {

    Optional<DailyHealth> findByDate(LocalDate date);

    @Query("""
        UPDATE daily_health
        SET date = :date,
            steps = :steps,
            active_calories = :activeCalories,
            resting_calories = :restingCalories,
            resting_heart_rate = :restingHeartRate,
            hrv_ms = :hrvMs,
            spo2_pct = :spo2Pct,
            respiratory_rate = :respiratoryRate,
            wrist_temperature_c = :wristTemperatureC,
            vo2_max = :vo2Max,
            stand_hours = :standHours,
            exercise_minutes = :exerciseMinutes,
            sleep_total_min = :sleepTotalMin,
            sleep_rem_min = :sleepRemMin,
            sleep_deep_min = :sleepDeepMin,
            sleep_core_min = :sleepCoreMin,
            sleep_awake_min = :sleepAwakeMin,
            mindful_minutes = :mindfulMinutes,
            flights_climbed = :flightsClimbed,
            walking_distance_m = :walkingDistanceM,
            walking_heart_rate_bpm = :walkingHeartRateBpm,
            updated_at = CURRENT_TIMESTAMP
        WHERE id = :id
        """)
    long updateDailyHealth(
        @Id UUID id,
        LocalDate date,
        Integer steps,
        Integer activeCalories,
        Integer restingCalories,
        Integer restingHeartRate,
        BigDecimal hrvMs,
        BigDecimal spo2Pct,
        BigDecimal respiratoryRate,
        BigDecimal wristTemperatureC,
        BigDecimal vo2Max,
        Integer standHours,
        Integer exerciseMinutes,
        Integer sleepTotalMin,
        Integer sleepRemMin,
        Integer sleepDeepMin,
        Integer sleepCoreMin,
        Integer sleepAwakeMin,
        Integer mindfulMinutes,
        Integer flightsClimbed,
        BigDecimal walkingDistanceM,
        Integer walkingHeartRateBpm
    );
}
