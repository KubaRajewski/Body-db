package com.bodydb.importer.service;

import com.bodydb.exercise.domain.ExerciseSession;
import com.bodydb.exercise.domain.ExerciseSet;
import com.bodydb.exercise.repository.ExerciseSessionRepository;
import com.bodydb.exercise.repository.ExerciseSetRepository;
import com.bodydb.importer.dto.ExerciseImportDto;
import com.bodydb.importer.dto.ImportResultDto;
import com.bodydb.importer.dto.SimpleExerciseLogDto;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Singleton
public class ExerciseImportService {

    private final ExerciseSessionRepository sessionRepo;
    private final ExerciseSetRepository setRepo;

    public ExerciseImportService(ExerciseSessionRepository sessionRepo,
                                 ExerciseSetRepository setRepo) {
        this.sessionRepo = sessionRepo;
        this.setRepo = setRepo;
    }

    @Transactional
    public ImportResultDto importExercise(ExerciseImportDto dto) {
        if (dto.sets() == null || dto.sets().isEmpty())
            return ImportResultDto.of(0, 0);

        // Create session
        ExerciseSession session = new ExerciseSession();
        session.setId(UUID.randomUUID());
        session.setDate(dto.date() != null ? LocalDate.parse(dto.date()) : LocalDate.now());
        if (dto.startedAt() != null) session.setStartedAt(Instant.parse(dto.startedAt()));
        session.setTitle(dto.title());
        session.setNotes(dto.notes());
        sessionRepo.save(session);

        int order = 1;
        for (ExerciseImportDto.SetEntry s : dto.sets()) {
            ExerciseSet set = new ExerciseSet();
            set.setId(UUID.randomUUID());
            set.setSessionId(session.getId());
            set.setSetOrder(s.setOrder() > 0 ? s.setOrder() : order);
            set.setExerciseName(s.exercise());
            set.setReps(s.reps());
            if (s.weightKg() != null) set.setWeightKg(BigDecimal.valueOf(s.weightKg()));
            set.setDurationSec(s.durationSec());
            if (s.rpe() != null) set.setRpe(BigDecimal.valueOf(s.rpe()));
            set.setNotes(s.notes());
            setRepo.save(set);
            order++;
        }

        return ImportResultDto.of(dto.sets().size(), 0);
    }

    /**
     * Simple log: one exercise, N identical sets, rep range as string.
     * Called by the iOS Shortcut (List → Choose → Ask for weight flow).
     */
    @Transactional
    public ImportResultDto logExercise(SimpleExerciseLogDto dto) {
        ExerciseSession session = new ExerciseSession();
        session.setId(UUID.randomUUID());
        session.setDate(dto.date() != null ? LocalDate.parse(dto.date()) : LocalDate.now());
        session.setTitle(dto.exercise());
        session.setNotes(dto.sets() + " sets × " + dto.repRange() + " reps @ " + dto.weightKg() + " kg");
        sessionRepo.save(session);

        for (int i = 1; i <= dto.sets(); i++) {
            ExerciseSet set = new ExerciseSet();
            set.setId(UUID.randomUUID());
            set.setSessionId(session.getId());
            set.setSetOrder(i);
            set.setExerciseName(dto.exercise());
            set.setWeightKg(BigDecimal.valueOf(dto.weightKg()));
            set.setNotes(dto.repRange() + " reps");
            setRepo.save(set);
        }

        return ImportResultDto.of(dto.sets(), 0);
    }
}
