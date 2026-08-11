/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.jpa.repository.JpaRepository
 *  org.springframework.data.jpa.repository.Query
 */
package com.caiofagundes.gymtracker.repository;

import com.caiofagundes.gymtracker.domain.PlannedSet;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PlannedSetRepository
extends JpaRepository<PlannedSet, Long> {
    public List<PlannedSet> findByExerciseIdOrderByOrderIndexAscIdAsc(Long var1);

    @Query(value="select p from PlannedSet p\nwhere p.id = :id and p.exercise.workout.plan.user.id = :userId\n")
    public Optional<PlannedSet> findByIdAndOwner(Long var1, Long var2);

    @Query(value="select coalesce(max(p.orderIndex), -1) from PlannedSet p\nwhere p.exercise.id = :exerciseId\n")
    public int maxOrderIndexForExercise(Long var1);
}

