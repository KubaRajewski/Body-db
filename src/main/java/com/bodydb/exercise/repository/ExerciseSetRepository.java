package com.bodydb.exercise.repository;

import com.bodydb.exercise.domain.ExerciseSet;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ExerciseSetRepository extends CrudRepository<ExerciseSet, UUID> {
    List<ExerciseSet> findBySessionId(UUID sessionId);

    @Query("SELECT es.* FROM exercise_sets es " +
           "JOIN exercise_sessions sess ON sess.id = es.session_id " +
           "WHERE sess.date BETWEEN :from AND :to " +
           "ORDER BY sess.date, es.set_order")
    List<ExerciseSet> findBySessionDateBetween(LocalDate from, LocalDate to);
}
