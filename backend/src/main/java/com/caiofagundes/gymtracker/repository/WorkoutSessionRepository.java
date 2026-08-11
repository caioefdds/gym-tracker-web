/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.jpa.repository.JpaRepository
 *  org.springframework.data.jpa.repository.Query
 */
package com.caiofagundes.gymtracker.repository;

import com.caiofagundes.gymtracker.domain.WorkoutSession;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WorkoutSessionRepository
extends JpaRepository<WorkoutSession, Long> {
    @Query(value="select s from WorkoutSession s\nwhere s.id = :id and s.plan.user.id = :userId\n")
    public Optional<WorkoutSession> findByIdAndOwner(Long var1, Long var2);
}

