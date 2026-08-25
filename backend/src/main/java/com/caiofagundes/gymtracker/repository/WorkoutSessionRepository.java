package com.caiofagundes.gymtracker.repository;

import com.caiofagundes.gymtracker.domain.WorkoutSession;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {
    @Query("select s from WorkoutSession s where s.id = :id and s.plan.user.id = :userId")
    Optional<WorkoutSession> findByIdAndOwner(@Param("id") Long id, @Param("userId") Long userId);

    @Query("""
            select s.id, w.id, w.name, s.startedAt, s.finishedAt, count(l.id)
            from WorkoutSession s
            join s.workout w
            left join SetLog l on l.session = s
            where s.plan.id = :planId and s.plan.user.id = :userId
            group by s.id, w.id, w.name, s.startedAt, s.finishedAt
            order by coalesce(s.finishedAt, s.startedAt) desc
            """)
    List<Object[]> summarizeByPlan(@Param("planId") Long planId, @Param("userId") Long userId);
}
