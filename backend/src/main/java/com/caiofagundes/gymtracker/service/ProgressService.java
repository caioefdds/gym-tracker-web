/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.stereotype.Service
 *  org.springframework.transaction.annotation.Transactional
 */
package com.caiofagundes.gymtracker.service;

import com.caiofagundes.gymtracker.common.NotFoundException;
import com.caiofagundes.gymtracker.repository.SetLogRepository;
import com.caiofagundes.gymtracker.repository.WorkoutPlanRepository;
import com.caiofagundes.gymtracker.web.dto.ProgressDtos;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProgressService {
    private final SetLogRepository setLogs;
    private final WorkoutPlanRepository plans;

    public ProgressService(SetLogRepository setLogs, WorkoutPlanRepository plans) {
        this.setLogs = setLogs;
        this.plans = plans;
    }

    @Transactional(readOnly=true)
    public List<ProgressDtos.ExerciseSummary> exercisesWithLogs(Long userId, Long planId) {
        this.plans.findByIdAndUserId(planId, userId).orElseThrow(() -> new NotFoundException("Ficha n\u00e3o encontrada"));
        List<Object[]> rows = this.setLogs.exercisesWithLogsInPlan(planId, userId);
        return rows.stream().map(r -> new ProgressDtos.ExerciseSummary(((Number)r[0]).longValue(), (String)r[2], ((Number)r[1]).longValue())).toList();
    }

    @Transactional(readOnly=true)
    public ProgressDtos.ExerciseProgress progress(Long userId, Long planId, Long exerciseId) {
        this.plans.findByIdAndUserId(planId, userId).orElseThrow(() -> new NotFoundException("Ficha n\u00e3o encontrada"));
        List<Object[]> rows = this.setLogs.progressForExercise(planId, exerciseId, userId);
        List<ProgressDtos.ProgressPoint> points = rows.stream().map(r -> {
            Timestamp ts = (Timestamp)r[0];
            OffsetDateTime date = ts.toInstant().atOffset(ZoneOffset.UTC);
            double maxWeight = ((Number)r[1]).doubleValue();
            double maxVolume = ((Number)r[2]).doubleValue();
            return new ProgressDtos.ProgressPoint(date, maxWeight, maxVolume);
        }).toList();
        return new ProgressDtos.ExerciseProgress(exerciseId, null, points);
    }
}

