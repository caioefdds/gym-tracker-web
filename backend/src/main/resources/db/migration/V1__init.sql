CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(120) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE workout_plans (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(120) NOT NULL,
    start_date DATE NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    KEY idx_workout_plans_user (user_id),
    CONSTRAINT fk_workout_plans_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE workouts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    plan_id BIGINT NOT NULL,
    name VARCHAR(120) NOT NULL,
    order_index INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_workouts_plan (plan_id),
    CONSTRAINT fk_workouts_plan FOREIGN KEY (plan_id) REFERENCES workout_plans (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE exercises (
    id BIGINT NOT NULL AUTO_INCREMENT,
    workout_id BIGINT NOT NULL,
    name VARCHAR(120) NOT NULL,
    order_index INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_exercises_workout (workout_id),
    CONSTRAINT fk_exercises_workout FOREIGN KEY (workout_id) REFERENCES workouts (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE planned_sets (
    id BIGINT NOT NULL AUTO_INCREMENT,
    exercise_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    reps_min INT NOT NULL,
    reps_max INT NOT NULL,
    order_index INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_planned_sets_exercise (exercise_id),
    CONSTRAINT fk_planned_sets_exercise FOREIGN KEY (exercise_id) REFERENCES exercises (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE workout_sessions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    plan_id BIGINT NOT NULL,
    workout_id BIGINT NOT NULL,
    started_at DATETIME(6) NOT NULL,
    finished_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    KEY idx_workout_sessions_plan (plan_id),
    KEY idx_workout_sessions_workout (workout_id),
    KEY idx_workout_sessions_started (started_at),
    CONSTRAINT fk_workout_sessions_plan FOREIGN KEY (plan_id) REFERENCES workout_plans (id) ON DELETE CASCADE,
    CONSTRAINT fk_workout_sessions_workout FOREIGN KEY (workout_id) REFERENCES workouts (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE set_logs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    session_id BIGINT NOT NULL,
    planned_set_id BIGINT NOT NULL,
    weight_kg DOUBLE NOT NULL,
    performed_reps INT NOT NULL,
    logged_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    KEY idx_set_logs_session (session_id),
    KEY idx_set_logs_planned_set (planned_set_id),
    KEY idx_set_logs_planned_logged (planned_set_id, logged_at),
    CONSTRAINT fk_set_logs_session FOREIGN KEY (session_id) REFERENCES workout_sessions (id) ON DELETE CASCADE,
    CONSTRAINT fk_set_logs_planned_set FOREIGN KEY (planned_set_id) REFERENCES planned_sets (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
