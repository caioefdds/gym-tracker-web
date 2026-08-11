/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.jpa.repository.JpaRepository
 *  org.springframework.data.jpa.repository.Query
 */
package com.caiofagundes.gymtracker.repository;

import com.caiofagundes.gymtracker.domain.Workout;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WorkoutRepository
extends JpaRepository<Workout, Long> {
    public List<Workout> findByPlanIdOrderByOrderIndexAscIdAsc(Long var1);

    @Query(value="select w from Workout w\nwhere w.id = :id and w.plan.user.id = :userId\n")
    public Optional<Workout> findByIdAndOwner(Long var1, Long var2);

    @Query(value="select coalesce(max(w.orderIndex), -1) from Workout w\nwhere w.plan.id = :planId\n")
    public int maxOrderIndexForPlan(Long var1);
}

