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
import lombok.Generated;

@Entity
@Table(name="workouts")
public class Workout {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="plan_id", nullable=false)
    private WorkoutPlan plan;
    @Column(nullable=false, length=120)
    private String name;
    @Column(name="order_index", nullable=false)
    private int orderIndex;

    @Generated
    public static WorkoutBuilder builder() {
        return new WorkoutBuilder();
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
    public String getName() {
        return this.name;
    }

    @Generated
    public int getOrderIndex() {
        return this.orderIndex;
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
    public void setName(String name) {
        this.name = name;
    }

    @Generated
    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    @Generated
    public Workout() {
    }

    @Generated
    public Workout(Long id, WorkoutPlan plan, String name, int orderIndex) {
        this.id = id;
        this.plan = plan;
        this.name = name;
        this.orderIndex = orderIndex;
    }

    @Generated
    public static class WorkoutBuilder {
        @Generated
        private Long id;
        @Generated
        private WorkoutPlan plan;
        @Generated
        private String name;
        @Generated
        private int orderIndex;

        @Generated
        WorkoutBuilder() {
        }

        @Generated
        public WorkoutBuilder id(Long id) {
            this.id = id;
            return this;
        }

        @Generated
        public WorkoutBuilder plan(WorkoutPlan plan) {
            this.plan = plan;
            return this;
        }

        @Generated
        public WorkoutBuilder name(String name) {
            this.name = name;
            return this;
        }

        @Generated
        public WorkoutBuilder orderIndex(int orderIndex) {
            this.orderIndex = orderIndex;
            return this;
        }

        @Generated
        public Workout build() {
            return new Workout(this.id, this.plan, this.name, this.orderIndex);
        }

        @Generated
        public String toString() {
            return "Workout.WorkoutBuilder(id=" + this.id + ", plan=" + String.valueOf(this.plan) + ", name=" + this.name + ", orderIndex=" + this.orderIndex + ")";
        }
    }
}

