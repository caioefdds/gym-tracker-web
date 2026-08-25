package com.caiofagundes.gymtracker.repository;

import com.caiofagundes.gymtracker.domain.PlannedSet;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlannedSetRepository extends JpaRepository<PlannedSet, Long> {
    List<PlannedSet> findByExerciseIdOrderByOrderIndexAscIdAsc(Long exerciseId);

    @Query("select p from PlannedSet p where p.id = :id and p.exercise.workout.plan.user.id = :userId")
    Optional<PlannedSet> findByIdAndOwner(@Param("id") Long id, @Param("userId") Long userId);

    @Query("select coalesce(max(p.orderIndex), -1) from PlannedSet p where p.exercise.id = :exerciseId")
    int maxOrderIndexForExercise(@Param("exerciseId") Long exerciseId);
}
