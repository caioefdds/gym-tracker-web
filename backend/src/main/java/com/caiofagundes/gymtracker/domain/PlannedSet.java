/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.persistence.Column
 *  jakarta.persistence.Entity
 *  jakarta.persistence.EnumType
 *  jakarta.persistence.Enumerated
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

import com.caiofagundes.gymtracker.domain.Exercise;
import com.caiofagundes.gymtracker.domain.SetType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Generated;

@Entity
@Table(name="planned_sets")
public class PlannedSet {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="exercise_id", nullable=false)
    private Exercise exercise;
    @Enumerated(value=EnumType.STRING)
    @Column(nullable=false, length=20)
    private SetType type;
    @Column(name="reps_min", nullable=false)
    private int repsMin;
    @Column(name="reps_max", nullable=false)
    private int repsMax;
    @Column(name="order_index", nullable=false)
    private int orderIndex;

    @Generated
    public static PlannedSetBuilder builder() {
        return new PlannedSetBuilder();
    }

    @Generated
    public Long getId() {
        return this.id;
    }

    @Generated
    public Exercise getExercise() {
        return this.exercise;
    }

    @Generated
    public SetType getType() {
        return this.type;
    }

    @Generated
    public int getRepsMin() {
        return this.repsMin;
    }

    @Generated
    public int getRepsMax() {
        return this.repsMax;
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
    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    @Generated
    public void setType(SetType type) {
        this.type = type;
    }

    @Generated
    public void setRepsMin(int repsMin) {
        this.repsMin = repsMin;
    }

    @Generated
    public void setRepsMax(int repsMax) {
        this.repsMax = repsMax;
    }

    @Generated
    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    @Generated
    public PlannedSet() {
    }

    @Generated
    public PlannedSet(Long id, Exercise exercise, SetType type, int repsMin, int repsMax, int orderIndex) {
        this.id = id;
        this.exercise = exercise;
        this.type = type;
        this.repsMin = repsMin;
        this.repsMax = repsMax;
        this.orderIndex = orderIndex;
    }

    @Generated
    public static class PlannedSetBuilder {
        @Generated
        private Long id;
        @Generated
        private Exercise exercise;
        @Generated
        private SetType type;
        @Generated
        private int repsMin;
        @Generated
        private int repsMax;
        @Generated
        private int orderIndex;

        @Generated
        PlannedSetBuilder() {
        }

        @Generated
        public PlannedSetBuilder id(Long id) {
            this.id = id;
            return this;
        }

        @Generated
        public PlannedSetBuilder exercise(Exercise exercise) {
            this.exercise = exercise;
            return this;
        }

        @Generated
        public PlannedSetBuilder type(SetType type) {
            this.type = type;
            return this;
        }

        @Generated
        public PlannedSetBuilder repsMin(int repsMin) {
            this.repsMin = repsMin;
            return this;
        }

        @Generated
        public PlannedSetBuilder repsMax(int repsMax) {
            this.repsMax = repsMax;
            return this;
        }

        @Generated
        public PlannedSetBuilder orderIndex(int orderIndex) {
            this.orderIndex = orderIndex;
            return this;
        }

        @Generated
        public PlannedSet build() {
            return new PlannedSet(this.id, this.exercise, this.type, this.repsMin, this.repsMax, this.orderIndex);
        }

        @Generated
        public String toString() {
            return "PlannedSet.PlannedSetBuilder(id=" + this.id + ", exercise=" + String.valueOf(this.exercise) + ", type=" + String.valueOf((Object)this.type) + ", repsMin=" + this.repsMin + ", repsMax=" + this.repsMax + ", orderIndex=" + this.orderIndex + ")";
        }
    }
}

