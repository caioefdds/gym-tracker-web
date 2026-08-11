/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.jpa.repository.JpaRepository
 *  org.springframework.data.jpa.repository.Query
 */
package com.caiofagundes.gymtracker.repository;

import com.caiofagundes.gymtracker.domain.Exercise;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExerciseRepository
extends JpaRepository<Exercise, Long> {
    public List<Exercise> findByWorkoutIdOrderByOrderIndexAscIdAsc(Long var1);

    @Query(value="select e from Exercise e\nwhere e.id = :id and e.workout.plan.user.id = :userId\n")
    public Optional<Exercise> findByIdAndOwner(Long var1, Long var2);

    @Query(value="select coalesce(max(e.orderIndex), -1) from Exercise e\nwhere e.workout.id = :workoutId\n")
    public int maxOrderIndexForWorkout(Long var1);
}

