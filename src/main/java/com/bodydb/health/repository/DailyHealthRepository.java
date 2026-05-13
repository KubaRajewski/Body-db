package com.bodydb.health.repository;

import com.bodydb.health.domain.DailyHealth;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface DailyHealthRepository extends CrudRepository<DailyHealth, UUID> {
    Optional<DailyHealth> findByDate(LocalDate date);
    List<DailyHealth> findByDateBetween(LocalDate from, LocalDate to);
}
