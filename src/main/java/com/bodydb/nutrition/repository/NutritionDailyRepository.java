package com.bodydb.nutrition.repository;

import com.bodydb.nutrition.domain.NutritionDaily;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface NutritionDailyRepository extends CrudRepository<NutritionDaily, UUID> {
    Optional<NutritionDaily> findByDate(LocalDate date);
    List<NutritionDaily> findByDateBetween(LocalDate from, LocalDate to);
}
