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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Singleton
public class ExerciseImportService {

    private static final Logger log = LoggerFactory.getLogger(ExerciseImportService.class);

    private final ExerciseSessionRepository sessionRepo;
    private final ExerciseSetRepository setRepo;

    public ExerciseImportService(ExerciseSessionRepository sessionRepo,
                                 ExerciseSetRepository setRepo) {
        this.sessionRepo = sessionRepo;
        this.setRepo = setRepo;
    }

    @Transactional
    public ImportResultDto importExercise(ExerciseImportDto dto) {
        if (dto.sets() == null || dto.sets().isEmpty()) {
            log.warn("importExercise called with empty sets list");
            return ImportResultDto.of(0, 0);
        }

        log.info("Starting exercise import — title='{}', date={}, {} set(s)",
                dto.title(), dto.date(), dto.sets().size());

        ExerciseSession session = new ExerciseSession();
        session.setId(UUID.randomUUID());
        session.setDate(dto.date() != null ? LocalDate.parse(dto.date()) : LocalDate.now());
        if (dto.startedAt() != null) session.setStartedAt(Instant.parse(dto.startedAt()));
        session.setTitle(dto.title());
        session.setNotes(dto.notes());
        sessionRepo.save(session);
        log.debug("Created session id={}", session.getId());

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
            log.debug("Saved set #{}: {} — {}kg x{} reps",
                    set.getSetOrder(), s.exercise(), s.weightKg(), s.reps());
            order++;
        }

        log.info("Exercise import done — session '{}', {} sets saved", dto.title(), dto.sets().size());
        return ImportResultDto.of(dto.sets().size(), 0);
    }

    /**
     * Simple log: one exercise, N identical sets, rep range as string.
     * Called by the iOS Shortcut (List → Choose → Ask for weight flow).
     */
    @Transactional
    public ImportResultDto logExercise(SimpleExerciseLogDto dto) {
        log.info("Logging exercise: {} — {} sets × {} @ {}kg",
                dto.exercise(), dto.sets(), dto.repRange(), dto.weightKg());

        ExerciseSession session = new ExerciseSession();
        session.setId(UUID.randomUUID());
        session.setDate(dto.date() != null ? LocalDate.parse(dto.date()) : LocalDate.now());
        session.setTitle(dto.exercise());
        session.setNotes(dto.sets() + " sets × " + dto.repRange() + " reps @ " + dto.weightKg() + " kg");
        sessionRepo.save(session);
        log.debug("Created session id={} for {}", session.getId(), dto.exercise());

        for (int i = 1; i <= dto.sets(); i++) {
            ExerciseSet set = new ExerciseSet();
            set.setId(UUID.randomUUID());
            set.setSessionId(session.getId());
            set.setSetOrder(i);
            set.setExerciseName(dto.exercise());
            set.setWeightKg(BigDecimal.valueOf(dto.weightKg()));
            set.setNotes(dto.repRange() + " reps");
            setRepo.save(set);
            log.debug("Saved set #{}/{}", i, dto.sets());
        }

        log.info("Exercise log done — {} × {} sets saved", dto.exercise(), dto.sets());
        return ImportResultDto.of(dto.sets(), 0);
    }
}
