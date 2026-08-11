/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.jpa.repository.JpaRepository
 *  org.springframework.data.jpa.repository.Query
 */
package com.caiofagundes.gymtracker.repository;

import com.caiofagundes.gymtracker.domain.SetLog;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SetLogRepository
extends JpaRepository<SetLog, Long> {
    public List<SetLog> findBySessionIdOrderByLoggedAtAsc(Long var1);

    @Query(value="select l from SetLog l\nwhere l.id = :id and l.session.plan.user.id = :userId\n")
    public Optional<SetLog> findByIdAndOwner(Long var1, Long var2);

    @Query(value="select l from SetLog l\nwhere l.plannedSet.id = :plannedSetId\n  and l.session.id <> :excludeSessionId\norder by l.loggedAt desc\nlimit 1\n")
    public Optional<SetLog> findLastForPlannedSet(Long var1, Long var2);

    @Query(value="SELECT COALESCE(s.finished_at, s.started_at) AS session_date,\n       MAX(l.weight_kg)                       AS max_weight,\n       MAX(l.weight_kg * l.performed_reps)    AS max_volume\nFROM workout_sessions s\nJOIN set_logs l ON l.session_id = s.id\nJOIN planned_sets p ON p.id = l.planned_set_id\nJOIN workout_plans wp ON wp.id = s.plan_id\nWHERE s.plan_id = :planId\n  AND p.exercise_id = :exerciseId\n  AND wp.user_id = :userId\nGROUP BY s.id\nORDER BY session_date ASC\n", nativeQuery=true)
    public List<Object[]> progressForExercise(Long var1, Long var2, Long var3);

    @Query(value="SELECT DISTINCT e.id, e.workout_id, e.name, e.order_index, w.order_index AS w_order\nFROM exercises e\nJOIN planned_sets p ON p.exercise_id = e.id\nJOIN set_logs l ON l.planned_set_id = p.id\nJOIN workout_sessions s ON s.id = l.session_id\nJOIN workouts w ON w.id = e.workout_id\nJOIN workout_plans wp ON wp.id = s.plan_id\nWHERE s.plan_id = :planId\n  AND wp.user_id = :userId\nORDER BY w_order, e.order_index, e.id\n", nativeQuery=true)
    public List<Object[]> exercisesWithLogsInPlan(Long var1, Long var2);
}

