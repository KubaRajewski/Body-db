package com.bodydb.importer.service;

import com.bodydb.importer.dto.ImportResultDto;
import com.bodydb.importer.dto.WorkoutExportDto;
import com.bodydb.importer.dto.WorkoutExportDto.WorkoutEntry;
import com.bodydb.importer.dto.WorkoutExportDto.StatValue;
import com.bodydb.workout.domain.Workout;
import com.bodydb.workout.repository.WorkoutRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.UUID;

@Singleton
public class WorkoutImportService {

    private static final Logger log = LoggerFactory.getLogger(WorkoutImportService.class);

    private static final String HK_ACTIVE   = "HKQuantityTypeIdentifierActiveEnergyBurned";
    private static final String HK_HR       = "HKQuantityTypeIdentifierHeartRate";
    private static final String HK_DIST_RUN = "HKQuantityTypeIdentifierDistanceWalkingRunning";
    private static final String HK_DIST_CYC = "HKQuantityTypeIdentifierDistanceCycling";

    private final WorkoutRepository repo;

    public WorkoutImportService(WorkoutRepository repo) { this.repo = repo; }

    @Transactional
    public ImportResultDto importWorkouts(WorkoutExportDto dto) {
        if (dto.workouts() == null) {
            log.warn("Workout import called with null workouts list");
            return ImportResultDto.of(0, 0);
        }

        log.info("Starting workout import — {} workout(s) received", dto.workouts().size());

        int inserted = 0, skipped = 0;
        for (WorkoutEntry entry : dto.workouts()) {
            String externalId = entry.startDate();
            if (externalId != null && repo.findByExternalId(externalId).isPresent()) {
                log.debug("Skipping duplicate workout: {} at {}", entry.activityType(), externalId);
                skipped++;
                continue;
            }
            Workout w = toEntity(entry);
            repo.save(w);
            log.debug("Inserted workout: {} on {} ({} min)", entry.activityType(), w.getDate(), w.getDurationMin());
            inserted++;
        }

        log.info("Workout import done — {} inserted, {} skipped (duplicates)", inserted, skipped);
        return ImportResultDto.of(inserted, skipped);
    }

    private Workout toEntity(WorkoutEntry e) {
        Workout w = new Workout();
        w.setId(UUID.randomUUID());
        w.setType(e.activityType());
        w.setDurationMin((int) Math.round(e.duration() / 60.0));
        w.setSource(e.source() != null ? e.source() : "apple_watch");
        w.setExternalId(e.startDate());

        if (e.startDate() != null) {
            Instant start = Instant.parse(e.startDate());
            w.setStartedAt(start);
            w.setDate(start.atZone(java.time.ZoneOffset.UTC).toLocalDate());
        }

        if (e.statistics() != null) {
            StatValue active = e.statistics().get(HK_ACTIVE);
            if (active != null && active.sum() != null)
                w.setCalories((int) Math.round(active.sum()));

            StatValue hr = e.statistics().get(HK_HR);
            if (hr != null) {
                if (hr.average() != null) w.setAvgHeartRate((int) Math.round(hr.average()));
                if (hr.max() != null)     w.setMaxHeartRate((int) Math.round(hr.max()));
            }

            StatValue distRun = e.statistics().get(HK_DIST_RUN);
            StatValue distCyc = e.statistics().get(HK_DIST_CYC);
            StatValue dist = distRun != null ? distRun : distCyc;
            if (dist != null && dist.sum() != null)
                w.setDistanceM((int) Math.round(dist.sum()));
        }
        return w;
    }
}
