/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.stereotype.Service
 *  org.springframework.transaction.annotation.Transactional
 */
package com.caiofagundes.gymtracker.service;

import com.caiofagundes.gymtracker.common.NotFoundException;
import com.caiofagundes.gymtracker.domain.Exercise;
import com.caiofagundes.gymtracker.domain.PlannedSet;
import com.caiofagundes.gymtracker.domain.Workout;
import com.caiofagundes.gymtracker.domain.WorkoutPlan;
import com.caiofagundes.gymtracker.repository.ExerciseRepository;
import com.caiofagundes.gymtracker.repository.PlannedSetRepository;
import com.caiofagundes.gymtracker.repository.WorkoutPlanRepository;
import com.caiofagundes.gymtracker.repository.WorkoutRepository;
import com.caiofagundes.gymtracker.user.User;
import com.caiofagundes.gymtracker.user.UserRepository;
import com.caiofagundes.gymtracker.web.dto.PlanDtos;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlanService {
    private final WorkoutPlanRepository plans;
    private final WorkoutRepository workouts;
    private final ExerciseRepository exercises;
    private final PlannedSetRepository plannedSets;
    private final UserRepository users;

    public PlanService(WorkoutPlanRepository plans, WorkoutRepository workouts, ExerciseRepository exercises, PlannedSetRepository plannedSets, UserRepository users) {
        this.plans = plans;
        this.workouts = workouts;
        this.exercises = exercises;
        this.plannedSets = plannedSets;
        this.users = users;
    }

    @Transactional(readOnly=true)
    public List<PlanDtos.PlanResponse> listPlans(Long userId) {
        return this.plans.findByUserIdOrderByIsActiveDescStartDateDesc(userId).stream().map(this::toPlanResponse).toList();
    }

    @Transactional
    public PlanDtos.PlanResponse createPlan(Long userId, PlanDtos.PlanRequest req) {
        User user = (User)this.users.findById(userId).orElseThrow(() -> new NotFoundException("Usu\u00e1rio n\u00e3o encontrado"));
        WorkoutPlan plan = WorkoutPlan.builder().user(user).name(req.name().trim()).startDate(req.startDate()).isActive(req.isActive()).build();
        plan = (WorkoutPlan)this.plans.save(plan);
        return this.toPlanResponse(plan);
    }

    @Transactional
    public PlanDtos.PlanResponse updatePlan(Long userId, Long planId, PlanDtos.PlanRequest req) {
        WorkoutPlan plan = this.requirePlan(userId, planId);
        plan.setName(req.name().trim());
        plan.setStartDate(req.startDate());
        plan.setActive(req.isActive());
        return this.toPlanResponse(plan);
    }

    @Transactional
    public void deletePlan(Long userId, Long planId) {
        WorkoutPlan plan = this.requirePlan(userId, planId);
        this.plans.delete(plan);
    }

    @Transactional(readOnly=true)
    public PlanDtos.PlanDetailResponse getPlanDetail(Long userId, Long planId) {
        WorkoutPlan plan = this.requirePlan(userId, planId);
        List<Workout> ws = this.workouts.findByPlanIdOrderByOrderIndexAscIdAsc(planId);
        List<PlanDtos.WorkoutNode> nodes = ws.stream().map(w -> {
            List<Exercise> exs = this.exercises.findByWorkoutIdOrderByOrderIndexAscIdAsc(w.getId());
            List<PlanDtos.ExerciseNode> exNodes = exs.stream().map(e -> {
                List<PlannedSet> ps = this.plannedSets.findByExerciseIdOrderByOrderIndexAscIdAsc(e.getId());
                List<PlanDtos.PlannedSetNode> psNodes = ps.stream().map(p -> new PlanDtos.PlannedSetNode(p.getId(), p.getType(), p.getRepsMin(), p.getRepsMax(), p.getOrderIndex())).toList();
                return new PlanDtos.ExerciseNode(e.getId(), e.getName(), e.getOrderIndex(), psNodes);
            }).toList();
            return new PlanDtos.WorkoutNode(w.getId(), w.getName(), w.getOrderIndex(), exNodes);
        }).toList();
        return new PlanDtos.PlanDetailResponse(plan.getId(), plan.getName(), plan.getStartDate(), plan.isActive(), nodes);
    }

    @Transactional
    public PlanDtos.WorkoutNode createWorkout(Long userId, Long planId, PlanDtos.WorkoutRequest req) {
        WorkoutPlan plan = this.requirePlan(userId, planId);
        int next = this.workouts.maxOrderIndexForPlan(planId) + 1;
        Workout w = Workout.builder().plan(plan).name(req.name().trim()).orderIndex(next).build();
        w = (Workout)this.workouts.save(w);
        return new PlanDtos.WorkoutNode(w.getId(), w.getName(), w.getOrderIndex(), List.of());
    }

    @Transactional
    public PlanDtos.WorkoutNode updateWorkout(Long userId, Long workoutId, PlanDtos.WorkoutUpdate req) {
        Workout w = this.workouts.findByIdAndOwner(workoutId, userId).orElseThrow(() -> new NotFoundException("Treino n\u00e3o encontrado"));
        w.setName(req.name().trim());
        w.setOrderIndex(req.orderIndex());
        return new PlanDtos.WorkoutNode(w.getId(), w.getName(), w.getOrderIndex(), List.of());
    }

    @Transactional
    public void deleteWorkout(Long userId, Long workoutId) {
        Workout w = this.workouts.findByIdAndOwner(workoutId, userId).orElseThrow(() -> new NotFoundException("Treino n\u00e3o encontrado"));
        this.workouts.delete(w);
    }

    @Transactional
    public PlanDtos.ExerciseNode createExercise(Long userId, Long workoutId, PlanDtos.ExerciseRequest req) {
        Workout w = this.workouts.findByIdAndOwner(workoutId, userId).orElseThrow(() -> new NotFoundException("Treino n\u00e3o encontrado"));
        int next = this.exercises.maxOrderIndexForWorkout(workoutId) + 1;
        Exercise e = Exercise.builder().workout(w).name(req.name().trim()).orderIndex(next).build();
        e = (Exercise)this.exercises.save(e);
        return new PlanDtos.ExerciseNode(e.getId(), e.getName(), e.getOrderIndex(), List.of());
    }

    @Transactional
    public PlanDtos.ExerciseNode updateExercise(Long userId, Long exerciseId, PlanDtos.ExerciseUpdate req) {
        Exercise e = this.exercises.findByIdAndOwner(exerciseId, userId).orElseThrow(() -> new NotFoundException("Exerc\u00edcio n\u00e3o encontrado"));
        e.setName(req.name().trim());
        e.setOrderIndex(req.orderIndex());
        return new PlanDtos.ExerciseNode(e.getId(), e.getName(), e.getOrderIndex(), List.of());
    }

    @Transactional
    public void deleteExercise(Long userId, Long exerciseId) {
        Exercise e = this.exercises.findByIdAndOwner(exerciseId, userId).orElseThrow(() -> new NotFoundException("Exerc\u00edcio n\u00e3o encontrado"));
        this.exercises.delete(e);
    }

    @Transactional
    public PlanDtos.PlannedSetNode createPlannedSet(Long userId, Long exerciseId, PlanDtos.PlannedSetRequest req) {
        Exercise ex = this.exercises.findByIdAndOwner(exerciseId, userId).orElseThrow(() -> new NotFoundException("Exerc\u00edcio n\u00e3o encontrado"));
        if (req.repsMax() < req.repsMin()) {
            throw new IllegalArgumentException("repsMax deve ser >= repsMin");
        }
        int next = this.plannedSets.maxOrderIndexForExercise(exerciseId) + 1;
        PlannedSet p = PlannedSet.builder().exercise(ex).type(req.type()).repsMin(req.repsMin()).repsMax(req.repsMax()).orderIndex(next).build();
        p = (PlannedSet)this.plannedSets.save(p);
        return new PlanDtos.PlannedSetNode(p.getId(), p.getType(), p.getRepsMin(), p.getRepsMax(), p.getOrderIndex());
    }

    @Transactional
    public PlanDtos.PlannedSetNode updatePlannedSet(Long userId, Long plannedSetId, PlanDtos.PlannedSetUpdate req) {
        PlannedSet p = this.plannedSets.findByIdAndOwner(plannedSetId, userId).orElseThrow(() -> new NotFoundException("S\u00e9rie n\u00e3o encontrada"));
        if (req.repsMax() < req.repsMin()) {
            throw new IllegalArgumentException("repsMax deve ser >= repsMin");
        }
        p.setType(req.type());
        p.setRepsMin(req.repsMin());
        p.setRepsMax(req.repsMax());
        p.setOrderIndex(req.orderIndex());
        return new PlanDtos.PlannedSetNode(p.getId(), p.getType(), p.getRepsMin(), p.getRepsMax(), p.getOrderIndex());
    }

    @Transactional
    public void deletePlannedSet(Long userId, Long plannedSetId) {
        PlannedSet p = this.plannedSets.findByIdAndOwner(plannedSetId, userId).orElseThrow(() -> new NotFoundException("S\u00e9rie n\u00e3o encontrada"));
        this.plannedSets.delete(p);
    }

    private WorkoutPlan requirePlan(Long userId, Long planId) {
        return this.plans.findByIdAndUserId(planId, userId).orElseThrow(() -> new NotFoundException("Ficha n\u00e3o encontrada"));
    }

    private PlanDtos.PlanResponse toPlanResponse(WorkoutPlan plan) {
        return new PlanDtos.PlanResponse(plan.getId(), plan.getName(), plan.getStartDate(), plan.isActive());
    }
}

