package com.caiofagundes.gymtracker.repository;

import com.caiofagundes.gymtracker.domain.Workout;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    List<Workout> findByPlanIdOrderByOrderIndexAscIdAsc(Long planId);

    @Query("select w from Workout w where w.id = :id and w.plan.user.id = :userId")
    Optional<Workout> findByIdAndOwner(@Param("id") Long id, @Param("userId") Long userId);

    @Query("select coalesce(max(w.orderIndex), -1) from Workout w where w.plan.id = :planId")
    int maxOrderIndexForPlan(@Param("planId") Long planId);
}
