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

import com.caiofagundes.gymtracker.domain.PlannedSet;
import com.caiofagundes.gymtracker.domain.WorkoutSession;
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
@Table(name="set_logs")
public class SetLog {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="session_id", nullable=false)
    private WorkoutSession session;
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="planned_set_id", nullable=false)
    private PlannedSet plannedSet;
    @Column(name="weight_kg", nullable=false)
    private double weightKg;
    @Column(name="performed_reps", nullable=false)
    private int performedReps;
    @Column(name="logged_at", nullable=false, updatable=false, insertable=false)
    private OffsetDateTime loggedAt;

    @Generated
    public static SetLogBuilder builder() {
        return new SetLogBuilder();
    }

    @Generated
    public Long getId() {
        return this.id;
    }

    @Generated
    public WorkoutSession getSession() {
        return this.session;
    }

    @Generated
    public PlannedSet getPlannedSet() {
        return this.plannedSet;
    }

    @Generated
    public double getWeightKg() {
        return this.weightKg;
    }

    @Generated
    public int getPerformedReps() {
        return this.performedReps;
    }

    @Generated
    public OffsetDateTime getLoggedAt() {
        return this.loggedAt;
    }

    @Generated
    public void setId(Long id) {
        this.id = id;
    }

    @Generated
    public void setSession(WorkoutSession session) {
        this.session = session;
    }

    @Generated
    public void setPlannedSet(PlannedSet plannedSet) {
        this.plannedSet = plannedSet;
    }

    @Generated
    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    @Generated
    public void setPerformedReps(int performedReps) {
        this.performedReps = performedReps;
    }

    @Generated
    public void setLoggedAt(OffsetDateTime loggedAt) {
        this.loggedAt = loggedAt;
    }

    @Generated
    public SetLog() {
    }

    @Generated
    public SetLog(Long id, WorkoutSession session, PlannedSet plannedSet, double weightKg, int performedReps, OffsetDateTime loggedAt) {
        this.id = id;
        this.session = session;
        this.plannedSet = plannedSet;
        this.weightKg = weightKg;
        this.performedReps = performedReps;
        this.loggedAt = loggedAt;
    }

    @Generated
    public static class SetLogBuilder {
        @Generated
        private Long id;
        @Generated
        private WorkoutSession session;
        @Generated
        private PlannedSet plannedSet;
        @Generated
        private double weightKg;
        @Generated
        private int performedReps;
        @Generated
        private OffsetDateTime loggedAt;

        @Generated
        SetLogBuilder() {
        }

        @Generated
        public SetLogBuilder id(Long id) {
            this.id = id;
            return this;
        }

        @Generated
        public SetLogBuilder session(WorkoutSession session) {
            this.session = session;
            return this;
        }

        @Generated
        public SetLogBuilder plannedSet(PlannedSet plannedSet) {
            this.plannedSet = plannedSet;
            return this;
        }

        @Generated
        public SetLogBuilder weightKg(double weightKg) {
            this.weightKg = weightKg;
            return this;
        }

        @Generated
        public SetLogBuilder performedReps(int performedReps) {
            this.performedReps = performedReps;
            return this;
        }

        @Generated
        public SetLogBuilder loggedAt(OffsetDateTime loggedAt) {
            this.loggedAt = loggedAt;
            return this;
        }

        @Generated
        public SetLog build() {
            return new SetLog(this.id, this.session, this.plannedSet, this.weightKg, this.performedReps, this.loggedAt);
        }

        @Generated
        public String toString() {
            return "SetLog.SetLogBuilder(id=" + this.id + ", session=" + String.valueOf(this.session) + ", plannedSet=" + String.valueOf(this.plannedSet) + ", weightKg=" + this.weightKg + ", performedReps=" + this.performedReps + ", loggedAt=" + String.valueOf(this.loggedAt) + ")";
        }
    }
}

