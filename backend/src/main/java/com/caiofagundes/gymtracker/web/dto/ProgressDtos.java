/*
 * Decompiled with CFR 0.152.
 */
package com.caiofagundes.gymtracker.web.dto;

import java.time.OffsetDateTime;
import java.util.List;

public class ProgressDtos {

    public record ExerciseProgress(Long exerciseId, String exerciseName, List<ProgressPoint> points) {
    }

    public record ProgressPoint(OffsetDateTime date, double maxWeight, double maxVolume) {
    }

    public record ExerciseSummary(Long id, String name, Long workoutId) {
    }
}

