package com.bodydb.exercise.repository;

import com.bodydb.exercise.domain.ExerciseSession;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ExerciseSessionRepository extends CrudRepository<ExerciseSession, UUID> {
    List<ExerciseSession> findByDateBetween(LocalDate from, LocalDate to);
}
