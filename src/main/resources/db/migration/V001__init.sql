-- daily_health: one record per day, data from HealthKit
CREATE TABLE daily_health (
    id UUID PRIMARY KEY,
    date DATE NOT NULL UNIQUE,
    steps INTEGER,
    active_calories INTEGER,
    resting_calories INTEGER,
    resting_heart_rate INTEGER,
    hrv_ms NUMERIC(6,2),
    spo2_pct NUMERIC(5,2),
    respiratory_rate NUMERIC(5,2),
    wrist_temperature_delta_c NUMERIC(4,2),
    vo2_max NUMERIC(5,2),
    stand_hours INTEGER,
    exercise_minutes INTEGER,
    sleep_total_min INTEGER,
    sleep_rem_min INTEGER,
    sleep_deep_min INTEGER,
    sleep_core_min INTEGER,
    sleep_awake_min INTEGER,
    mindful_minutes INTEGER,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- workouts: Apple Watch data, many per day
CREATE TABLE workouts (
    id UUID PRIMARY KEY,
    date DATE NOT NULL,
    started_at TIMESTAMPTZ,
    type VARCHAR(64) NOT NULL,
    duration_min INTEGER NOT NULL,
    calories INTEGER,
    avg_heart_rate INTEGER,
    max_heart_rate INTEGER,
    distance_m INTEGER,
    source VARCHAR(32) NOT NULL DEFAULT 'apple_watch',
    external_id VARCHAR(128),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_workouts_date ON workouts(date);
CREATE UNIQUE INDEX idx_workouts_external_id ON workouts(external_id) WHERE external_id IS NOT NULL;

-- nutrition_daily: daily macros
CREATE TABLE nutrition_daily (
    id UUID PRIMARY KEY,
    date DATE NOT NULL UNIQUE,
    calories_kcal INTEGER,
    protein_g NUMERIC(6,2),
    carbs_g NUMERIC(6,2),
    fat_g NUMERIC(6,2),
    fiber_g NUMERIC(6,2),
    sugar_g NUMERIC(6,2),
    sodium_mg INTEGER,
    water_ml INTEGER,
    source VARCHAR(32) NOT NULL DEFAULT 'cronometer',
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- exercise_sessions: manual strength session
CREATE TABLE exercise_sessions (
    id UUID PRIMARY KEY,
    date DATE NOT NULL,
    started_at TIMESTAMPTZ,
    ended_at TIMESTAMPTZ,
    title VARCHAR(128),
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_exercise_sessions_date ON exercise_sessions(date);

-- exercise_sets: individual set within a session
CREATE TABLE exercise_sets (
    id UUID PRIMARY KEY,
    session_id UUID NOT NULL REFERENCES exercise_sessions(id) ON DELETE CASCADE,
    set_order INTEGER NOT NULL,
    exercise_name VARCHAR(128) NOT NULL,
    reps INTEGER,
    weight_kg NUMERIC(6,2),
    duration_sec INTEGER,
    distance_m INTEGER,
    rpe NUMERIC(3,1),
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_exercise_sets_session ON exercise_sets(session_id);

-- auto-update updated_at
CREATE OR REPLACE FUNCTION touch_updated_at() RETURNS TRIGGER AS $$
BEGIN NEW.updated_at = now(); RETURN NEW; END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_daily_health_updated_at BEFORE UPDATE ON daily_health
    FOR EACH ROW EXECUTE FUNCTION touch_updated_at();
CREATE TRIGGER tr_nutrition_daily_updated_at BEFORE UPDATE ON nutrition_daily
    FOR EACH ROW EXECUTE FUNCTION touch_updated_at();
CREATE TRIGGER tr_exercise_sessions_updated_at BEFORE UPDATE ON exercise_sessions
    FOR EACH ROW EXECUTE FUNCTION touch_updated_at();
