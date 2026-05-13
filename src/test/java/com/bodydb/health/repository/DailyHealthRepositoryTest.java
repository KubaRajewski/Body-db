package com.bodydb.health.repository;

import com.bodydb.health.domain.DailyHealth;
import com.bodydb.support.PostgresIntegrationTestSupport;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest(startApplication = false, transactional = false)
class DailyHealthRepositoryTest extends PostgresIntegrationTestSupport {

    @Inject
    DailyHealthRepository dailyHealthRepository;

    @BeforeEach
    void cleanDatabase() {
        dailyHealthRepository.deleteAll();
    }

    @Test
    void insertAndFindByDate() {
        DailyHealth dailyHealth = new DailyHealth();
        dailyHealth.setId(UUID.randomUUID());
        dailyHealth.setDate(LocalDate.of(2026, 4, 28));
        dailyHealth.setSteps(12_345);
        dailyHealth.setActiveCalories(650);
        dailyHealth.setRestingHeartRate(52);
        dailyHealth.setHrvMs(new BigDecimal("47.50"));

        dailyHealthRepository.save(dailyHealth);

        DailyHealth found = dailyHealthRepository.findByDate(LocalDate.of(2026, 4, 28)).orElseThrow();
        assertThat(found.getSteps()).isEqualTo(12_345);
        assertThat(found.getActiveCalories()).isEqualTo(650);
        assertThat(found.getHrvMs()).isEqualByComparingTo("47.50");
    }

    @Test
    void updateViaEntityUpdate() {
        DailyHealth dailyHealth = new DailyHealth();
        dailyHealth.setId(UUID.randomUUID());
        dailyHealth.setDate(LocalDate.of(2026, 4, 28));
        dailyHealth.setSteps(1_000);

        DailyHealth saved = dailyHealthRepository.save(dailyHealth);

        saved.setSteps(15_000);
        saved.setActiveCalories(720);
        saved.setRestingHeartRate(49);
        saved.setHrvMs(new BigDecimal("55.25"));
        saved.setStandHours(10);
        saved.setExerciseMinutes(75);
        saved.setSleepTotalMin(460);
        saved.setMindfulMinutes(20);
        dailyHealthRepository.update(saved);

        DailyHealth updated = dailyHealthRepository.findByDate(LocalDate.of(2026, 4, 28)).orElseThrow();
        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getSteps()).isEqualTo(15_000);
        assertThat(updated.getActiveCalories()).isEqualTo(720);
        assertThat(updated.getRestingHeartRate()).isEqualTo(49);
        assertThat(updated.getHrvMs()).isEqualByComparingTo("55.25");
        assertThat(updated.getStandHours()).isEqualTo(10);
        assertThat(updated.getExerciseMinutes()).isEqualTo(75);
        assertThat(updated.getSleepTotalMin()).isEqualTo(460);
        assertThat(updated.getMindfulMinutes()).isEqualTo(20);
    }
}
