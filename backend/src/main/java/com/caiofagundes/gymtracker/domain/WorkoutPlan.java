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

import com.caiofagundes.gymtracker.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import lombok.Generated;

@Entity
@Table(name="workout_plans")
public class WorkoutPlan {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="user_id", nullable=false)
    private User user;
    @Column(nullable=false, length=120)
    private String name;
    @Column(name="start_date", nullable=false)
    private LocalDate startDate;
    @Column(name="is_active", nullable=false)
    private boolean isActive;
    @Column(name="created_at", nullable=false, updatable=false, insertable=false)
    private OffsetDateTime createdAt;

    @Generated
    public static WorkoutPlanBuilder builder() {
        return new WorkoutPlanBuilder();
    }

    @Generated
    public Long getId() {
        return this.id;
    }

    @Generated
    public User getUser() {
        return this.user;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public LocalDate getStartDate() {
        return this.startDate;
    }

    @Generated
    public boolean isActive() {
        return this.isActive;
    }

    @Generated
    public OffsetDateTime getCreatedAt() {
        return this.createdAt;
    }

    @Generated
    public void setId(Long id) {
        this.id = id;
    }

    @Generated
    public void setUser(User user) {
        this.user = user;
    }

    @Generated
    public void setName(String name) {
        this.name = name;
    }

    @Generated
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    @Generated
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Generated
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Generated
    public WorkoutPlan() {
    }

    @Generated
    public WorkoutPlan(Long id, User user, String name, LocalDate startDate, boolean isActive, OffsetDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.startDate = startDate;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    @Generated
    public static class WorkoutPlanBuilder {
        @Generated
        private Long id;
        @Generated
        private User user;
        @Generated
        private String name;
        @Generated
        private LocalDate startDate;
        @Generated
        private boolean isActive;
        @Generated
        private OffsetDateTime createdAt;

        @Generated
        WorkoutPlanBuilder() {
        }

        @Generated
        public WorkoutPlanBuilder id(Long id) {
            this.id = id;
            return this;
        }

        @Generated
        public WorkoutPlanBuilder user(User user) {
            this.user = user;
            return this;
        }

        @Generated
        public WorkoutPlanBuilder name(String name) {
            this.name = name;
            return this;
        }

        @Generated
        public WorkoutPlanBuilder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        @Generated
        public WorkoutPlanBuilder isActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        @Generated
        public WorkoutPlanBuilder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        @Generated
        public WorkoutPlan build() {
            return new WorkoutPlan(this.id, this.user, this.name, this.startDate, this.isActive, this.createdAt);
        }

        @Generated
        public String toString() {
            return "WorkoutPlan.WorkoutPlanBuilder(id=" + this.id + ", user=" + String.valueOf(this.user) + ", name=" + this.name + ", startDate=" + String.valueOf(this.startDate) + ", isActive=" + this.isActive + ", createdAt=" + String.valueOf(this.createdAt) + ")";
        }
    }
}

