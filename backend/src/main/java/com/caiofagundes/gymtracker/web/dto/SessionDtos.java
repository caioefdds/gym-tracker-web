/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.validation.constraints.Min
 *  jakarta.validation.constraints.NotNull
 *  jakarta.validation.constraints.Positive
 */
package com.caiofagundes.gymtracker.web.dto;

import com.caiofagundes.gymtracker.domain.SetType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.OffsetDateTime;
import java.util.List;

public class SessionDtos {

    public record SetLogResponse(Long id, Long plannedSetId, double weightKg, int performedReps, OffsetDateTime loggedAt) {
    }

    public record SetLogUpdate(@Positive double weightKg, @Min(value=1L) @Min(value=1L) int performedReps) {
    }

    public record SetLogRequest(@NotNull Long plannedSetId, @Positive double weightKg, @Min(value=1L) @Min(value=1L) int performedReps) {
    }

    public record StartSessionResponse(Long sessionId) {
    }

    public record SessionResponse(Long sessionId, Long planId, Long workoutId, String workoutName, OffsetDateTime startedAt, OffsetDateTime finishedAt, List<SessionExerciseNode> exercises) {
    }

    public record CurrentLog(Long id, double weightKg, int performedReps, OffsetDateTime loggedAt) {
    }

    public record LastLog(double weightKg, int performedReps, OffsetDateTime loggedAt) {
    }

    public record SessionPlannedSet(Long plannedSetId, SetType type, int repsMin, int repsMax, int orderIndex, LastLog lastTime, CurrentLog currentLog) {
    }

    public record SessionExerciseNode(Long exerciseId, String exerciseName, int orderIndex, List<SessionPlannedSet> sets) {
    }
}

