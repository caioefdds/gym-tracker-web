package com.caiofagundes.gymtracker.repository;

import com.caiofagundes.gymtracker.domain.Exercise;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByWorkoutIdOrderByOrderIndexAscIdAsc(Long workoutId);

    @Query("select e from Exercise e where e.id = :id and e.workout.plan.user.id = :userId")
    Optional<Exercise> findByIdAndOwner(@Param("id") Long id, @Param("userId") Long userId);

    @Query("select coalesce(max(e.orderIndex), -1) from Exercise e where e.workout.id = :workoutId")
    int maxOrderIndexForWorkout(@Param("workoutId") Long workoutId);
}
