/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.jpa.repository.JpaRepository
 */
package com.caiofagundes.gymtracker.repository;

import com.caiofagundes.gymtracker.domain.WorkoutPlan;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutPlanRepository
extends JpaRepository<WorkoutPlan, Long> {
    public List<WorkoutPlan> findByUserIdOrderByIsActiveDescStartDateDesc(Long var1);

    public Optional<WorkoutPlan> findByIdAndUserId(Long var1, Long var2);
}

