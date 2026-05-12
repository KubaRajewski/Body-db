ALTER TABLE daily_health
    ADD COLUMN flights_climbed INTEGER,
    ADD COLUMN walking_distance_m NUMERIC(10, 2),
    ADD COLUMN walking_heart_rate_bpm INTEGER;

ALTER TABLE daily_health
    RENAME COLUMN wrist_temperature_delta_c TO wrist_temperature_c;
