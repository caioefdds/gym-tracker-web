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
@Table(name="exercises")
public class Exercise {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="workout_id", nullable=false)
    private Workout workout;
    @Column(nullable=false, length=120)
    private String name;
    @Column(name="order_index", nullable=false)
    private int orderIndex;

    @Generated
    public static ExerciseBuilder builder() {
        return new ExerciseBuilder();
    }

    @Generated
    public Long getId() {
        return this.id;
    }

    @Generated
    public Workout getWorkout() {
        return this.workout;
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
    public void setWorkout(Workout workout) {
        this.workout = workout;
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
    public Exercise() {
    }

    @Generated
    public Exercise(Long id, Workout workout, String name, int orderIndex) {
        this.id = id;
        this.workout = workout;
        this.name = name;
        this.orderIndex = orderIndex;
    }

    @Generated
    public static class ExerciseBuilder {
        @Generated
        private Long id;
        @Generated
        private Workout workout;
        @Generated
        private String name;
        @Generated
        private int orderIndex;

        @Generated
        ExerciseBuilder() {
        }

        @Generated
        public ExerciseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        @Generated
        public ExerciseBuilder workout(Workout workout) {
            this.workout = workout;
            return this;
        }

        @Generated
        public ExerciseBuilder name(String name) {
            this.name = name;
            return this;
        }

        @Generated
        public ExerciseBuilder orderIndex(int orderIndex) {
            this.orderIndex = orderIndex;
            return this;
        }

        @Generated
        public Exercise build() {
            return new Exercise(this.id, this.workout, this.name, this.orderIndex);
        }

        @Generated
        public String toString() {
            return "Exercise.ExerciseBuilder(id=" + this.id + ", workout=" + String.valueOf(this.workout) + ", name=" + this.name + ", orderIndex=" + this.orderIndex + ")";
        }
    }
}

