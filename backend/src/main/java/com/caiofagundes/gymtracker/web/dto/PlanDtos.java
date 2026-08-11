/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.validation.Valid
 *  jakarta.validation.constraints.Min
 *  jakarta.validation.constraints.NotBlank
 *  jakarta.validation.constraints.NotNull
 *  jakarta.validation.constraints.Size
 */
package com.caiofagundes.gymtracker.web.dto;

import com.caiofagundes.gymtracker.domain.SetType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public class PlanDtos {

    public record ReorderRequest(@Valid @NotNull List<Long> orderedIds) {
    }

    public record PlannedSetUpdate(@NotNull SetType type, @Min(value=1L) @Min(value=1L) int repsMin, @Min(value=1L) @Min(value=1L) int repsMax, @Min(value=0L) @Min(value=0L) int orderIndex) {
    }

    public record PlannedSetRequest(@NotNull SetType type, @Min(value=1L) @Min(value=1L) int repsMin, @Min(value=1L) @Min(value=1L) int repsMax) {
    }

    public record ExerciseUpdate(@NotBlank @Size(min=1, max=120) @NotBlank @Size(min=1, max=120) String name, @Min(value=0L) @Min(value=0L) int orderIndex) {
    }

    public record ExerciseRequest(@NotBlank @Size(min=1, max=120) @NotBlank @Size(min=1, max=120) String name) {
    }

    public record WorkoutUpdate(@NotBlank @Size(min=1, max=120) @NotBlank @Size(min=1, max=120) String name, @Min(value=0L) @Min(value=0L) int orderIndex) {
    }

    public record WorkoutRequest(@NotBlank @Size(min=1, max=120) @NotBlank @Size(min=1, max=120) String name) {
    }

    public record PlannedSetNode(Long id, SetType type, int repsMin, int repsMax, int orderIndex) {
    }

    public record ExerciseNode(Long id, String name, int orderIndex, List<PlannedSetNode> plannedSets) {
    }

    public record WorkoutNode(Long id, String name, int orderIndex, List<ExerciseNode> exercises) {
    }

    public record PlanDetailResponse(Long id, String name, LocalDate startDate, boolean isActive, List<WorkoutNode> workouts) {
    }

    public record PlanResponse(Long id, String name, LocalDate startDate, boolean isActive) {
    }

    public record PlanRequest(@NotBlank @Size(min=1, max=120) @NotBlank @Size(min=1, max=120) String name, @NotNull LocalDate startDate, boolean isActive) {
    }
}

