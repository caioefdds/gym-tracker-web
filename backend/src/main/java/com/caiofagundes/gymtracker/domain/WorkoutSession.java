/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.persistence.Column
 *  jakarta.persistence.Entity
 *  jakarta.persistence.FetchType
 *  jakarta.persistence.GeneratedValue
 *  jakarta.persistence.GenerationType
 *  jakarta.persistence.Id
 *  jakarta.persistence.JoinColumn
 *  jakarta.persistence.ManyToOne
 *  jakarta.persistence.Table
 *  lombok.Generated
 */
package com.caiofagundes.gymtracker.domain;

import com.caiofagundes.gymtracker.domain.Workout;
import com.caiofagundes.gymtracker.domain.WorkoutPlan;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Generated;

@Entity
@Table(name="workout_sessions")
public class WorkoutSession {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="plan_id", nullable=false)
    private WorkoutPlan plan;
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="workout_id", nullable=false)
    private Workout workout;
    @Column(name="started_at", nullable=false)
    private OffsetDateTime startedAt;
    @Column(name="finished_at")
    private OffsetDateTime finishedAt;

    @Generated
    public static WorkoutSessionBuilder builder() {
        return new WorkoutSessionBuilder();
    }

    @Generated
    public Long getId() {
        return this.id;
    }

    @Generated
    public WorkoutPlan getPlan() {
        return this.plan;
    }

    @Generated
    public Workout getWorkout() {
        return this.workout;
    }

    @Generated
    public OffsetDateTime getStartedAt() {
        return this.startedAt;
    }

    @Generated
    public OffsetDateTime getFinishedAt() {
        return this.finishedAt;
    }

    @Generated
    public void setId(Long id) {
        this.id = id;
    }

    @Generated
    public void setPlan(WorkoutPlan plan) {
        this.plan = plan;
    }

    @Generated
    public void setWorkout(Workout workout) {
        this.workout = workout;
    }

    @Generated
    public void setStartedAt(OffsetDateTime startedAt) {
        this.startedAt = startedAt;
    }

    @Generated
    public void setFinishedAt(OffsetDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    @Generated
    public WorkoutSession() {
    }

    @Generated
    public WorkoutSession(Long id, WorkoutPlan plan, Workout workout, OffsetDateTime startedAt, OffsetDateTime finishedAt) {
        this.id = id;
        this.plan = plan;
        this.workout = workout;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
    }

    @Generated
    public static class WorkoutSessionBuilder {
        @Generated
        private Long id;
        @Generated
        private WorkoutPlan plan;
        @Generated
        private Workout workout;
        @Generated
        private OffsetDateTime startedAt;
        @Generated
        private OffsetDateTime finishedAt;

        @Generated
        WorkoutSessionBuilder() {
        }

        @Generated
        public WorkoutSessionBuilder id(Long id) {
            this.id = id;
            return this;
        }

        @Generated
        public WorkoutSessionBuilder plan(WorkoutPlan plan) {
            this.plan = plan;
            return this;
        }

        @Generated
        public WorkoutSessionBuilder workout(Workout workout) {
            this.workout = workout;
            return this;
        }

        @Generated
        public WorkoutSessionBuilder startedAt(OffsetDateTime startedAt) {
            this.startedAt = startedAt;
            return this;
        }

        @Generated
        public WorkoutSessionBuilder finishedAt(OffsetDateTime finishedAt) {
            this.finishedAt = finishedAt;
            return this;
        }

        @Generated
        public WorkoutSession build() {
            return new WorkoutSession(this.id, this.plan, this.workout, this.startedAt, this.finishedAt);
        }

        @Generated
        public String toString() {
            return "WorkoutSession.WorkoutSessionBuilder(id=" + this.id + ", plan=" + String.valueOf(this.plan) + ", workout=" + String.valueOf(this.workout) + ", startedAt=" + String.valueOf(this.startedAt) + ", finishedAt=" + String.valueOf(this.finishedAt) + ")";
        }
    }
}

