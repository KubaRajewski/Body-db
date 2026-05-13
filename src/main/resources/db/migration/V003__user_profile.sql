-- user_profile: single-user profile + long-term goals / strategy
CREATE TABLE user_profile (
    id                     UUID PRIMARY KEY,
    height_cm              INTEGER,
    current_weight_kg      NUMERIC(5,2),
    target_weight_kg       NUMERIC(5,2),
    age                    INTEGER,
    sex                    VARCHAR(16),
    goal_description       TEXT,           -- e.g. "Lean bulk, target 85kg, increase muscle mass"
    daily_kcal_target      INTEGER,
    protein_target_g       INTEGER,
    training_days_per_week INTEGER,
    notes                  TEXT,           -- free-form strategy / preferences for Claude
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TRIGGER tr_user_profile_updated_at BEFORE UPDATE ON user_profile
    FOR EACH ROW EXECUTE FUNCTION touch_updated_at();
