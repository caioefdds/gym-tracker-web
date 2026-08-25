package com.caiofagundes.gymtracker.repository;

import com.caiofagundes.gymtracker.domain.SetLog;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SetLogRepository extends JpaRepository<SetLog, Long> {
    List<SetLog> findBySessionIdOrderByLoggedAtAsc(Long sessionId);

    @Query("select l from SetLog l where l.id = :id and l.session.plan.user.id = :userId")
    Optional<SetLog> findByIdAndOwner(@Param("id") Long id, @Param("userId") Long userId);

    @Query("""
            select l from SetLog l
            where l.plannedSet.id = :plannedSetId
              and l.session.id <> :excludeSessionId
            order by l.loggedAt desc
            limit 1
            """)
    Optional<SetLog> findLastForPlannedSet(
            @Param("plannedSetId") Long plannedSetId,
            @Param("excludeSessionId") Long excludeSessionId);

    @Query("""
            select l from SetLog l
            join fetch l.session s
            join fetch l.plannedSet p
            join s.plan plan
            join plan.user u
            where s.workout.id = :workoutId
              and s.id <> :excludeSessionId
              and u.id = :userId
              and (p.exercise.id = :exerciseId or lower(p.exercise.name) = lower(:exerciseName))
            order by coalesce(s.finishedAt, s.startedAt) desc, p.orderIndex asc, l.id asc
            """)
    List<SetLog> findHistoryForExercise(
            @Param("workoutId") Long workoutId,
            @Param("exerciseId") Long exerciseId,
            @Param("exerciseName") String exerciseName,
            @Param("excludeSessionId") Long excludeSessionId,
            @Param("userId") Long userId);

    @Query(
            value = """
                    SELECT COALESCE(s.finished_at, s.started_at) AS session_date,
                           MAX(l.weight_kg) AS max_weight,
                           MAX(l.weight_kg * l.performed_reps) AS max_volume
                    FROM workout_sessions s
                    JOIN set_logs l ON l.session_id = s.id
                    JOIN planned_sets p ON p.id = l.planned_set_id
                    JOIN workout_plans wp ON wp.id = s.plan_id
                    WHERE s.plan_id = :planId
                      AND p.exercise_id = :exerciseId
                      AND wp.user_id = :userId
                    GROUP BY s.id
                    ORDER BY session_date ASC
                    """,
            nativeQuery = true)
    List<Object[]> progressForExercise(
            @Param("planId") Long planId,
            @Param("exerciseId") Long exerciseId,
            @Param("userId") Long userId);

    @Query(
            value = """
                    SELECT DISTINCT e.id, e.workout_id, e.name, e.order_index, w.order_index AS w_order
                    FROM exercises e
                    JOIN planned_sets p ON p.exercise_id = e.id
                    JOIN set_logs l ON l.planned_set_id = p.id
                    JOIN workout_sessions s ON s.id = l.session_id
                    JOIN workouts w ON w.id = e.workout_id
                    JOIN workout_plans wp ON wp.id = s.plan_id
                    WHERE s.plan_id = :planId
                      AND wp.user_id = :userId
                    ORDER BY w_order, e.order_index, e.id
                    """,
            nativeQuery = true)
    List<Object[]> exercisesWithLogsInPlan(
            @Param("planId") Long planId, @Param("userId") Long userId);
}
